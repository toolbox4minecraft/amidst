package amidst.map.layer;

import amidst.map.Fragment;
import amidst.minecraft.world.CoordinatesResolution;

public abstract class ImageLayer extends Layer {
	private final int layerId;
	private final CoordinatesResolution resolution;

	public ImageLayer(int layerId, CoordinatesResolution resolution) {
		this.layerId = layerId;
		this.resolution = resolution;
	}

	public float getAlpha() {
		return 1;
	}

	public int getLayerId() {
		return layerId;
	}

	public CoordinatesResolution getResolution() {
		return resolution;
	}

	public int getSize() {
		return Fragment.SIZE / resolution.getSize();
	}

	public double getScale() {
		return resolution.getSize();
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
