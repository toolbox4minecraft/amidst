package amidst.fragment;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;

import amidst.minecraft.world.BiomeDataOracle;
import amidst.minecraft.world.CoordinatesInWorld;
import amidst.minecraft.world.Resolution;
import amidst.minecraft.world.icon.WorldIcon;

/**
 * This class is thread-safe. It contains nearly no logic but only simple and
 * atomic getters and setters.
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
 */
public class Fragment {
	public static final int SIZE = Resolution.FRAGMENT.getStep();

	private volatile boolean isInitialized = false;
	private volatile boolean isLoaded = false;
	private volatile CoordinatesInWorld corner;

	private volatile float alpha;
	private volatile short[][] biomeData;
	private final AtomicReferenceArray<BufferedImage> images;
	private final AtomicReferenceArray<List<WorldIcon>> worldIcons;

	public Fragment(int numberOfLayers) {
		this.images = new AtomicReferenceArray<BufferedImage>(numberOfLayers);
		this.worldIcons = new AtomicReferenceArray<List<WorldIcon>>(
				numberOfLayers);
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

	public void populateBiomeData(BiomeDataOracle biomeDataOracle) {
		biomeDataOracle.populateArrayUsingQuarterResolution(corner, biomeData);
	}

	public short getBiomeDataAt(int x, int y) {
		return biomeData[x][y];
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

	public void setInitialized(boolean isInitialized) {
		this.isInitialized = isInitialized;
	}

	public boolean isInitialized() {
		return isInitialized;
	}

	public void setLoaded(boolean isLoaded) {
		this.isLoaded = isLoaded;
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
