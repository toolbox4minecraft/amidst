package amidst.map.layer;

import java.awt.image.BufferedImage;

import amidst.map.Fragment;
import amidst.minecraft.world.CoordinatesInWorld;
import amidst.minecraft.world.Resolution;

public abstract class ImageLayer extends Layer {
	private final int layerId;
	private final Resolution resolution;

	public ImageLayer(LayerType layerType, int layerId, Resolution resolution) {
		super(layerType);
		this.layerId = layerId;
		this.resolution = resolution;
	}

	public float getAlpha() {
		return 1;
	}

	public int getLayerId() {
		return layerId;
	}

	public Resolution getResolution() {
		return resolution;
	}

	public int getSize() {
		return Fragment.SIZE / resolution.getStep();
	}

	public double getScale() {
		return resolution.getStep();
	}

	public void drawToCache(Fragment fragment, int[] cache, BufferedImage image) {
		CoordinatesInWorld corner = fragment.getCorner();
		long cornerX = corner.getXAs(resolution);
		long cornerY = corner.getYAs(resolution);
		int size = getSize();
		drawToCache(fragment, cache, cornerX, cornerY, size);
		image.setRGB(0, 0, size, size, cache, 0, size);
	}

	protected void drawToCache(Fragment fragment, int[] cache, long cornerX,
			long cornerY, int size) {
		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {
				int index = getCacheIndex(x, y, size);
				cache[index] = getColorAt(fragment, cornerX + x, cornerY + y);
			}
		}
	}

	protected int getCacheIndex(int x, int y, int size) {
		return x + y * size;
	}

	protected abstract int getColorAt(Fragment fragment, long xAsResolution,
			long yAsResolution);
}
