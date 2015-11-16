package amidst.map.layer;

import java.awt.geom.AffineTransform;

import amidst.map.Fragment;

public abstract class ImageLayer extends Layer {
	private AffineTransform cachedScalingMatrix = new AffineTransform();

	private int size;
	private double scale;
	private int[] defaultData;

	private int layerId;

	public ImageLayer(int size) {
		this.size = size;
		initScale();
		initDefaultData();
	}

	private void initScale() {
		scale = Fragment.SIZE / (double) size;
	}

	private void initDefaultData() {
		defaultData = new int[getSquaredSize()];
		for (int i = 0; i < defaultData.length; i++) {
			defaultData[i] = 0x00000000;
		}
	}

	public void load(Fragment fragment) {
		drawToCache(fragment);
	}

	public AffineTransform getMatrix(AffineTransform inMat) {
		cachedScalingMatrix.setTransform(inMat);
		return cachedScalingMatrix;
	}

	public AffineTransform getScaledMatrix(AffineTransform inMat) {
		cachedScalingMatrix.setTransform(inMat);
		cachedScalingMatrix.scale(scale, scale);
		return cachedScalingMatrix;
	}

	protected int getSquaredSize() {
		return size * size;
	}

	public int getSize() {
		return size;
	}

	public int[] getDefaultData() {
		return defaultData;
	}

	public float getAlpha() {
		return 1;
	}

	public int getLayerId() {
		return layerId;
	}

	public void setLayerId(int layerId) {
		this.layerId = layerId;
	}

	public abstract void drawToCache(Fragment fragment);
}
