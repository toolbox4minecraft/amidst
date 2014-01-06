package amidst.map;

import java.awt.geom.AffineTransform;
import amidst.preferences.BooleanPrefModel;

public abstract class Layer {
	protected float alpha = 1.0f;
	protected double scale;
	protected int size;
	protected Map map;
	
	private AffineTransform cachedScalingMatrix = new AffineTransform();
	
	private int[] defaultData;
	
	public Layer(int size) {
		this.size = size;
		defaultData = new int[size*size];
		scale = Fragment.SIZE/(double)size;
		for (int i = 0; i < defaultData.length; i++)
			defaultData[i] = 0x00000000;
	}
	
	public void setMap(Map map) {
		this.map = map;
	}
	public Map getMap() {
		return map;
	}
	
	public boolean isVisible() {
		return true;
	}
	
	public int[] getDefaultData() {
		return defaultData;
	}
	
	public void load(Fragment frag, int layerID) {
		drawToCache(frag, layerID);
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
	
	public float getAlpha() {
		return alpha;
	}

	public abstract void drawToCache(Fragment fragment, int layerID);
}

