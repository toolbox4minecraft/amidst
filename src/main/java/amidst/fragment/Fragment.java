package amidst.fragment;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.StampedLock;

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
import amidst.util.AtomicFloat;

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
	
	private final StampedLock lock;

	private final AtomicBoolean isInitialized;
	private final AtomicBoolean isLoaded;
	private final AtomicFloat alpha;
	private final AtomicReference<CoordinatesInWorld> corner;
	
	private volatile short[][] biomeData;
	private volatile List<EndIsland> endIslands;
	private volatile BufferedImage[] images;
	private volatile List<WorldIcon>[] worldIcons;

	@SuppressWarnings("unchecked")
	public Fragment(int numberOfLayers) {
		this.lock = new StampedLock();
		this.isInitialized = new AtomicBoolean();
		this.isLoaded = new AtomicBoolean();
		this.alpha = new AtomicFloat();
		this.corner = new AtomicReference<CoordinatesInWorld>();
		this.images = new BufferedImage[numberOfLayers];
		this.worldIcons = new List[numberOfLayers];
	}
	
	public long writeLock() {
		return lock.writeLock();
	}
	
	public long readLock() {
		return lock.readLock();
	}
	
	public void unlock(long stamp) {
		lock.unlock(stamp);
	}

	public void setAlpha(float alpha) {
		this.alpha.set(alpha);
	}

	public float getAlpha() {
		return alpha.get();
	}

	public void initBiomeData(long stamp, int width, int height) {
		validateLock(stamp);
		biomeData = new short[width][height];
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	public void populateBiomeData(long stamp, BiomeDataOracle biomeDataOracle) {
		validateLock(stamp);
		biomeDataOracle.populateArray(getCorner(), biomeData, true);
	}

	public short getBiomeDataAt(long stamp, int x, int y) {
		validateLock(stamp);
		return biomeData[x][y];
	}

	public void setEndIslands(long stamp, List<EndIsland> endIslands) {
		validateLock(stamp);
		this.endIslands = endIslands;
	}

	public List<EndIsland> getEndIslands(long stamp) {
		validateLock(stamp);
		return endIslands;
	}

	public BufferedImage getAndSetImage(long stamp, int layerId, BufferedImage image) {
		validateLock(stamp);
		BufferedImage old = images[layerId];
		images[layerId] = image;
		return old;
	}

	public void putImage(long stamp, int layerId, BufferedImage image) {
		validateLock(stamp);
		images[layerId] = image;
	}

	public BufferedImage getImage(long stamp, int layerId) {
		validateLock(stamp);
		return images[layerId];
	} 

	public void putWorldIcons(long stamp, int layerId, List<WorldIcon> icons) {
		validateLock(stamp);
		worldIcons[layerId] = icons;
	}

	public List<WorldIcon> getWorldIcons(long stamp, int layerId) {
		validateLock(stamp);
		if (isLoaded.get()) {
			List<WorldIcon> result = worldIcons[layerId];
			if (result != null) {
				return result;
			}
		}
		return Collections.emptyList();
	}

	@CalledByAny
	public void setInitialized() {
		this.isInitialized.set(true);
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	public void setLoaded() {
		this.isLoaded.set(true);
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	public void recycle() {
		this.isLoaded.set(false);
		this.isInitialized.set(false);
	}

	public boolean isInitialized() {
		return isInitialized.get();
	}

	public boolean isLoaded() {
		return isLoaded.get();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void setCorner(CoordinatesInWorld corner) {
		this.corner.set(corner);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public CoordinatesInWorld getCorner() {
		return corner.get();
	}
	
	private void validateLock(long stamp) {
		if (!lock.validate(stamp)) throw new ConcurrentModificationException("invalid lock stamp");
	}
	
}
