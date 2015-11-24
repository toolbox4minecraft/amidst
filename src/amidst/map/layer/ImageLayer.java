package amidst.map.layer;

import java.awt.image.BufferedImage;

import amidst.map.Fragment;
import amidst.minecraft.world.CoordinatesInWorld;
import amidst.minecraft.world.Resolution;

public abstract class ImageLayer extends Layer {
	private final Resolution resolution;

	public ImageLayer(LayerType layerType, Resolution resolution) {
		super(layerType);
		this.resolution = resolution;
	}

	public float getAlpha() {
		return 1;
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

	@Override
	public void load(Fragment fragment, int[] imageCache) {
		CoordinatesInWorld corner = fragment.getCorner();
		long cornerX = corner.getXAs(resolution);
		long cornerY = corner.getYAs(resolution);
		int size = getSize();
		drawToCache(fragment, imageCache, cornerX, cornerY, size);
		BufferedImage image = fragment.getImage(getLayerType());
		image.setRGB(0, 0, size, size, imageCache, 0, size);
	}

	protected void drawToCache(Fragment fragment, int[] cache, long cornerX,
			long cornerY, int size) {
		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {
				int index = getCacheIndex(x, y, size);
				cache[index] = getColorAt(fragment, cornerX, cornerY, x, y);
			}
		}
	}

	private int getCacheIndex(int x, int y, int size) {
		return x + y * size;
	}

	protected abstract int getColorAt(Fragment fragment, long cornerX,
			long cornerY, int x, int y);
}
