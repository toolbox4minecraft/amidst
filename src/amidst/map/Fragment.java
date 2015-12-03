package amidst.map;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;

import amidst.Options;
import amidst.minecraft.world.BiomeDataOracle;
import amidst.minecraft.world.CoordinatesInWorld;
import amidst.minecraft.world.Resolution;
import amidst.minecraft.world.icon.WorldIcon;

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

	public boolean isInBounds(CoordinatesInWorld coordinates) {
		return coordinates.isInBoundsOf(corner, SIZE);
	}

	public void prepareLoad() {
		initAlpha();
	}

	public void prepareReload() {
	}

	public void prepareDraw(float time) {
		updateAlpha(time);
	}

	public void initAlpha() {
		alpha = Options.instance.mapFading.get() ? 0.0f : 1.0f;
	}

	public void updateAlpha(float time) {
		alpha = Math.min(1.0f, time * 3.0f + alpha);
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

	public String getBiomeAliasAt(CoordinatesInWorld coordinates,
			String defaultAlias) {
		if (!isLoaded) {
			return defaultAlias;
		}
		long x = coordinates.getXRelativeToFragmentAs(Resolution.QUARTER);
		long y = coordinates.getYRelativeToFragmentAs(Resolution.QUARTER);
		short biome = getBiomeDataAt((int) x, (int) y);
		return Options.instance.biomeColorProfile.getAliasForId(biome);
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
