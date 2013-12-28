package amidst.map;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.imageio.ImageIO;

import amidst.logging.Log;
import amidst.preferences.BooleanPrefModel;

public class Layer implements Comparable<Layer> {
	public String name;
	public int size;
	public float depth;
	public float minZoom = 0;
	public float maxZoom = 1024;
	
	protected float alpha = 1.0f;
	
	public double scale;
	private AffineTransform mat = new AffineTransform();
	
	private int[] defaultData;
	
	public boolean cacheEnabled;
	public CacheManager cacheManager;
	public String cachePath;
	
	protected Map map;
	
	private BooleanPrefModel visible = null;
	
	public boolean isTransparent;
	
	public Layer(String name) {
		this(name, null);
	}
	public Layer(String name, CacheManager cacheManager) {
		this(name, cacheManager, 1f);
	}
	public Layer(String name, CacheManager cacheManager, float depth) {
		this(name, cacheManager, depth, Fragment.SIZE);
	}
	public Layer(String name, CacheManager cacheManager, float depth, int size) {
		this.name = name;
		this.cacheManager = cacheManager;
		this.cacheEnabled = (cacheManager != null);
		this.depth = depth;
		this.size = size;
		defaultData = new int[size*size];
		scale = ((double)Fragment.SIZE)/((double)size);
		for (int i = 0; i < defaultData.length; i++)
			defaultData[i] = 0x00000000;
		isTransparent = true;
	}
	public void setMap(Map map) {
		this.map = map;
	}
	public Map getMap() {
		return map;
	}
	
	public boolean isVisible() {
		return (visible == null) || visible.get();
	}
	public void setVisibilityPref(BooleanPrefModel visibility) {
		visible = visibility;
	}
	
	public void unload(Fragment frag) {
		if (cacheEnabled) {
			cacheManager.unload(frag);
		}
	}
	
	
	
	public Layer setMaxZoom(float maxZoom) {
		this.maxZoom = maxZoom;
		return this;
	}
	public Layer setMinZoom(float minZoom) {
		this.minZoom = minZoom;
		return this;
	}
	
	public int compareTo(Layer obj) {
		Layer lObj = (Layer)obj;
		if (depth < lObj.depth) return -1;
		return (depth > lObj.depth)?1:0;
	}
	
	public int[] getDefaultData() {
		return defaultData;
	}
	
	public void load(Fragment frag, int layerID) {
		if (cacheEnabled) {
			cacheManager.load(frag, layerID);
		} else {
			drawToCache(frag, layerID);
			//PluginManager.call(funcDraw, frag, layerID);
		}
	}
	
	public AffineTransform getMatrix(AffineTransform inMat) {
		mat.setTransform(inMat);
		return mat;
	}
	public AffineTransform getScaledMatrix(AffineTransform inMat) {
		mat.setTransform(inMat); mat.scale(scale, scale);
		return mat;
	}
	public AffineTransform getScaledMatrix(AffineTransform inMat, float mipmapScale) {
		mat.setTransform(inMat); mat.scale(scale * mipmapScale, scale * mipmapScale);
		return mat;
	}
	
	public void drawToCache(Fragment fragment, int layerID) {
		
	}
	
	public void drawLive(Fragment fragment, Graphics2D g, AffineTransform mat) {
		
	}
	public float getAlpha() {
		return alpha;
	}
	
}

