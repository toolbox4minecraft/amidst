package amidst.fragment;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledByAny;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.gui.main.viewer.Drawer;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.mojangapi.world.icon.WorldIcon;
import amidst.mojangapi.world.oracle.BiomeDataOracle;
import amidst.mojangapi.world.oracle.EndIsland;

/**
 * This class contains nearly no logic but only simple and atomic getters and
 * setters.
 *
 * The life-cycle of a Fragment is quite complex to prevent the garbage
 * collection from running too often. When a fragment is no longer needed it
 * will be kept available in a queue, so it can be reused later on. The
 * life-cycle consists of the two flags isInitialized and isLoaded.
 * setInitialized(true) can be called from any thread, however
 * setInitialized(false), setLoaded(true) and setLoaded(false) will always be
 * called from the fragment loading thread, to ensure consistent state. Also,
 * setInitialized(true) will only be called again after setInitialized(false)
 * was called. It is not possible that isLoaded is true while isInitialized is
 * false.
 *
 * It is possible that a thread that uses the data in the fragment continues to
 * use them after isLoaded is set to false. However, all write operations are
 * only called from the fragment loading thread or during the construction of
 * the fragment. While the fragment is constructed it will only be accessible by
 * one thread. An exception to that rule is the instance variable alpha. It is
 * altered from the drawing thread, however this should not cause any issues.
 *
 * Immediately after a new instance of this class is created, it is passed to
 * all FragmentConstructors. At that point in time, no other thread can access
 * the fragment, so the whole construction process is single-threaded. After the
 * fragment is constructed it will be available to use in the fragment graph. As
 * soon as it is requested, its isInitialized variable will be set to true by
 * the requesting thread. Also, it is enqueued to the loading queue. Note, that
 * the fragment is still not loaded, but used in the fragment graph and thus
 * used by the {@link Drawer}. Sometime after the fragment was requested, it
 * will be loaded by the fragment loading thread, because it was enqueued to the
 * loading queue. The complete loading process is executed in the fragment
 * loading thread. When this is done, the isLoaded variable will be set to true.
 * This allows the drawer to actually draw the fragment. The complete drawing
 * process is executed in the event dispatch thread. When the fragment is no
 * longer visible on the screen it will be removed from the fragment graph.
 * However, since it holds a data-structure that is quite heavy to allocate and
 * garbage-collect, the fragment will be recycled so it can be reused later.
 * This recycling is done by enqueuing the fragment to the recycle queue. The
 * recycle queue is processed by the fragment loading thread with a very high
 * priority. Even tough the fragment loading thread only calls the method
 * {@link Fragment#recycle()} and enqueues the fragment to the available queue,
 * it is important that this is done by the fragment loading queue. This is,
 * because if any other thread sets the isLoaded variable to false, it might be
 * set to true by the fragment loading thread afterwards, because the fragment
 * was not yet loaded. This problem is solved by modifying the isLoaded variable
 * only in the fragment loading thread. This issue only arises, since the
 * fragment is already used in the fragment graph, before it is loaded. As soon
 * as it is used in the fragment graph it, can be recycled. This often leads to
 * a situation where a not yet loaded fragment gets recycled. The isInitialized
 * variable is altered by the thread that requests the new fragment, which is
 * different from the fragment loading thread. This is not an issue, since all
 * fragments in the available queue have both variables isInitialized and
 * isLoaded set to false. They are also not used in the fragment graph or
 * enqueued in the recycle queue. Therefore, there cannot be a race condition
 * because the isInitialized variable will only be set to false when it is
 * recycled.
 */
@NotThreadSafe
public class Fragment {
	public static final int SIZE = Resolution.FRAGMENT.getStep();

	private volatile boolean isInitialized = false;
	private volatile boolean isLoaded = false;
	private volatile CoordinatesInWorld corner;

	private volatile float alpha;
	private volatile short[][] biomeData;
	private volatile List<EndIsland> endIslands;
	private final AtomicReferenceArray<BufferedImage> images;
	private final AtomicReferenceArray<List<WorldIcon>> worldIcons;

	public Fragment(int numberOfLayers) {
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
		if (isLoaded) {
			List<WorldIcon> result = worldIcons.get(layerId);
			if (result != null) {
				return result;
			}
		}
		return Collections.emptyList();
	}

	@CalledByAny
	public void setInitialized() {
		this.isInitialized = true;
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	public void setLoaded() {
		this.isLoaded = true;
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	public void recycle() {
		this.isLoaded = false;
		this.isInitialized = false;
	}

	public boolean isInitialized() {
		return isInitialized;
	}

	public boolean isLoaded() {
		return isLoaded;
	}

	public void setCorner(CoordinatesInWorld corner) {
		this.corner = corner;
	}

	public CoordinatesInWorld getCorner() {
		return corner;
	}
}
