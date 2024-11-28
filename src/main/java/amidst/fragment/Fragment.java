package amidst.fragment;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.UnaryOperator;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.ThreadSafe;
import amidst.gui.main.viewer.Drawer;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.mojangapi.world.icon.WorldIcon;
import amidst.mojangapi.world.oracle.BiomeDataOracle;
import amidst.mojangapi.world.oracle.EndIsland;

/**
 * This class contains nearly no logic but only simple and atomic getters and
 * setters. </br>
 * </br>
 * The life-cycle of a Fragment is quite complex to prevent the garbage
 * collection from running too often. The cycle consists of four states:
 * uninitialized, initialized, loading, and loaded. These all govern what can
 * be done to the fragment and any given time. A fragment starts off as
 * uninitialized on creation. Immediately after a new fragment is created, it
 * is passed to all FragmentConstructors by the EDT. At that point in time, no
 * other thread can access the fragment, so the whole construction process is
 * single-threaded. After the fragment is constructed, its state gets set to
 * initialized, and is put in both the fragment graph in the loading queue. Note,
 * that the fragment is still not loaded, but used in the fragment graph and thus
 * used by the {@link Drawer}. Sometime after the fragment was requested, it
 * will go through the loading process because it was enqueued to the loading
 * queue. The fragment loader and worker threads are the only threads
 * that can modify its state beyond this point.</br>
 * </br>
 * During the loading process, the fragment loader thread will first check to
 * make sure that there are threads open in the fragment worker thread pool. If
 * it finds any threads that aren't executing anything, it will be passed to a
 * fragment worker to be loaded. When the fragment worker starts, it checks to
 * see if the fragment is already loading. If it is not, the isLoading variable
 * is set to true. This is to make sure that only one thread is loading the
 * fragment at a time. </br>
 * </br>
 * When this is done, the state will be set to loaded. This allows the
 * drawer to actually draw the fragment. The complete drawing process is
 * executed in the event dispatch thread. When the fragment is no longer visible
 * on the screen, a check is done to determine how to use it. If the fragment is
 * loading or loaded, it gets sent to the off-screen cache, where it will be able
 * to exist for a set amount of time without being refreshed before it gets
 * recycled. If the fragment isn't loaded or loading, it also gets recycled.
 * When a fragment is recycled, its state gets reset to initialized and it gets
 * sent to the available cache to be re-used. If a fragment stays in the available
 * queue for too long, it gets cleared and eventually garbage collected.</br>
 * </br>
 * It is possible that a thread that uses the data in the fragment continues to
 * use them after its state gets reset to uninitialized. However, all write
 * operations are called from either the fragment loading thread, the threads from
 * {@link FragmentManager#fragWorkers}, or the EDT during the construction of
 * the fragment. While the fragment is constructed it will only be accessible
 * by one thread. An exception to that rule is the instance variable alpha.
 * It is altered from the drawing thread, however this should not cause any
 * issues. </br>
 * </br>
 */
@ThreadSafe
public class Fragment {
	public static final int SIZE = Resolution.FRAGMENT.getStep();

	private final AtomicReference<State> state;
	private volatile CoordinatesInWorld corner;

	private volatile float alpha;
	private volatile short[][] biomeData;
	private volatile List<EndIsland> endIslands;
	private final AtomicReferenceArray<BufferedImage> images;
	private final AtomicReferenceArray<List<WorldIcon>> worldIcons;

	public Fragment(int numberOfLayers) {
		this.state = new AtomicReference<State>(State.UNINITIALIZED);
		this.images = new AtomicReferenceArray<>(numberOfLayers);
		this.worldIcons = new AtomicReferenceArray<>(numberOfLayers);
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	public float getAlpha() {
		return alpha;
	}

	public void initBiomeData(int width, int height) {
		biomeData = new short[width][height];
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	public void populateBiomeData(BiomeDataOracle biomeDataOracle) {
		int width = biomeData.length;
		int height = width == 0 ? 0 : biomeData[0].length;
		biomeDataOracle.getBiomeData(corner, width, height, true, data -> {
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					biomeData[i][j] = (short) data[j * width + i];
				}
			}
		});
	}

	public short getBiomeDataAt(int x, int y) {
		return biomeData[x][y];
	}

	public void setEndIslands(List<EndIsland> endIslands) {
		this.endIslands = endIslands;
	}

	public List<EndIsland> getEndIslands() {
		return endIslands;
	}

	public BufferedImage getAndSetImage(int layerId, BufferedImage image) {
		return images.getAndSet(layerId, image);
	}

	public void putImage(int layerId, BufferedImage image) {
		images.set(layerId, image);
	}

	public BufferedImage getImage(int layerId) {
		return images.get(layerId);
	}

	public void putWorldIcons(int layerId, List<WorldIcon> icons) {
		worldIcons.set(layerId, icons);
	}

	public List<WorldIcon> getWorldIcons(int layerId) {
		if (state.get().equals(State.LOADED)) {
			List<WorldIcon> result = worldIcons.get(layerId);
			if (result != null) {
				return result;
			}
		}
		return Collections.emptyList();
	}

	public void setState(State state) {
		this.state.set(state);
	}

	public State getState() {
		return this.state.get();
	}

	public State getAndSetState(State state) {
		return this.state.getAndSet(state);
	}

	public State updateAndGetState(UnaryOperator<State> updateFunction) {
		return this.state.updateAndGet(updateFunction);
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	// can only be recycled if it's not loading
	public boolean tryRecycle() {
		return this.state.updateAndGet(s -> s.equals(State.LOADING) ? State.LOADING : State.UNINITIALIZED).equals(State.UNINITIALIZED);
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	// can only be recycled if it's not loading
	public boolean tryRecycleNotLoaded() {
		return this.state.updateAndGet(s -> s.equals(State.LOADING) || s.equals(State.LOADED) ? s : State.UNINITIALIZED).equals(State.UNINITIALIZED);
	}

	public void setCorner(CoordinatesInWorld corner) {
		this.corner = corner;
	}

	public CoordinatesInWorld getCorner() {
		return corner;
	}
	
	public static enum State {
		UNINITIALIZED,
		INITIALIZED,
		LOADING,
		LOADED;
	}
}
