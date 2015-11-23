package amidst.map.layer;

import amidst.map.Fragment;

public abstract class ImageLayer extends Layer {
	private final int size;
	private final int layerId;
	private final double scale;

	public ImageLayer(int size, int layerId) {
		this.size = size;
		this.layerId = layerId;
		this.scale = Fragment.SIZE / (double) size;
	}

	public float getAlpha() {
		return 1;
	}

	public int getSize() {
		return size;
	}

	public int getLayerId() {
		return layerId;
	}

	public double getScale() {
		return scale;
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
