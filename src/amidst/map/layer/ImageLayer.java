package amidst.map.layer;

import amidst.map.Fragment;
import amidst.minecraft.world.Resolution;

public abstract class ImageLayer extends Layer {
	private final int layerId;
	private final Resolution resolution;

	public ImageLayer(int layerId, Resolution resolution) {
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

	public void drawToCache(Fragment fragment, int[] cache) {
		for (int blockY = 0; blockY < getSize(); blockY++) {
			for (int blockX = 0; blockX < getSize(); blockX++) {
				int i = blockY * getSize() + blockX;
				cache[i] = getColorAt(fragment, blockX, blockY);
			}
		}
	}

	protected abstract int getColorAt(Fragment fragment, int blockX, int blockY);
}
