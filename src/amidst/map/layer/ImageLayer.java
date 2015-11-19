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

	public void load(Fragment fragment) {
		drawToCache(fragment);
	}

	protected int getSquaredSize() {
		return size * size;
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

	public abstract void drawToCache(Fragment fragment);
}
