package amidst.map;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Vector;

import amidst.Log;

public class Fragment {
	public static final int SIZE = 512, SIZE_SHIFT = 9, MAX_OBJECTS_PER_FRAGMENT = 20, MIPMAP_LEVELS = 3;
	
	public int blockX, blockY;
	
	private Layer[] layers;
	private Layer[] liveLayers;
	private IconLayer[] iconLayers;
	
	private MipMapImage[] images;
	public MapObject[] objects;
	public int objectsLength = 0;
	
	public boolean isActive = false;
	public boolean isLoaded = false;
	
	public Fragment nextFragment = null, prevFragment = null;
	public boolean hasNext = false;
	
	public boolean endOfLine = false;
	
	public String lastLoadedThreadName;
	
	private static int[] dataCache = new int[SIZE*SIZE];
	
	
	public Fragment(Layer... layers) {
		this(layers, null, null);
	}
	public Fragment(Layer[] layers, Layer[] liveLayers, IconLayer[] iconLayers) {
		this.layers = layers;
		this.liveLayers = liveLayers;
		images = new MipMapImage[layers.length];
		for (int i = 0; i < layers.length; i++)
			images[i] = new MipMapImage(layers[i].size, layers[i].size, MIPMAP_LEVELS, layers[i].isTransparent);
		this.iconLayers = iconLayers;
		objects = new MapObject[MAX_OBJECTS_PER_FRAGMENT];
	}
	
	public void load() {
		if (isLoaded)
			Log.w("This should never happen!");
		lastLoadedThreadName = Thread.currentThread().getName();
		for (int i = 0; i < layers.length; i++)
			layers[i].load(this, i);
		for (int i = 0; i < iconLayers.length; i++)
			iconLayers[i].generateMapObjects(this);
		isLoaded = true;
	}
	
	public void recycle() {
		isActive = false;
		lastLoadedThreadName = "Recycled";
		if (isLoaded) {
			for (Layer layer : layers)
				layer.unload(this);
		}
		
		isLoaded = false;
	}
	
	public void clearData() {
		for (IconLayer layer : iconLayers)
			layer.clearMapObjects(this);
		isLoaded = false;
		for (int i = 0; i < images.length; i++)
			images[i].setData(layers[i].getDefaultData()); // TODO: Is this needed?		
	}
	
	public void clear() {
		for (IconLayer layer : iconLayers)
			layer.clearMapObjects(this);
		//isLoaded = false;
		hasNext = false;
		endOfLine = false;
		isActive = true;
		for (int i = 0; i < images.length; i++)
			images[i].setData(layers[i].getDefaultData()); // TODO: Is this needed?
	}
	public void drawLive(Graphics2D g, AffineTransform mat) {
		for (int i = 0; i < liveLayers.length; i++) {
			if (liveLayers[i].isVisible()) {
				liveLayers[i].drawLive(this, g, liveLayers[i].getMatrix(mat));
			}
		}
		
	}
	public void draw(Graphics2D g, AffineTransform mat) {
		for (int i = 0; i < images.length; i++) {
			if (layers[i].isVisible()) {
				int level = (int)Math.floor(Math.log((1./mat.getScaleX())/layers[i].scale) / Math.log(2));
				level = Math.min(MIPMAP_LEVELS - 1, Math.max(0, level));
				g.setTransform(layers[i].getScaledMatrix(mat, (float)(1 << level)));
				g.drawImage(images[i].getImage(level), 0, 0, null);
			}
		}
	}
	
	public void drawObjects(Graphics2D g, AffineTransform inMatrix) {
		AffineTransform drawMatrix = new AffineTransform();
		for (int i = 0; i < objectsLength; i++) {
			if (objects[i].parentLayer.isVisible()) {
				drawMatrix.setTransform(inMatrix);
				drawMatrix.translate(objects[i].x, objects[i].y);
				double invZoom = 1.0 / objects[i].parentLayer.map.getZoom();
				drawMatrix.scale(invZoom, invZoom);
				g.setTransform(drawMatrix);
				
				g.drawImage(objects[i].getImage(), 
						    -(objects[i].getWidth() >> 1),
						    -(objects[i].getHeight() >> 1),
						    objects[i].getWidth(),
						    objects[i].getHeight(),
						    null);
			}
		}
	}
	
	public void addObject(MapObject object) {
		object.rx = object.x + this.blockX;
		object.ry = object.y + this.blockY;
		objects[objectsLength] = object;
		objectsLength++;
	}
	
	public void setImageData(int layerID, int[] data) {
		images[layerID].setData(data);
	}
	
	public int getChunkX() {
		return blockX >> 4;
	}
	public int getChunkY() {
		return blockY >> 4;
	}
	public int getFragmentX() {
		return blockX >> SIZE_SHIFT;
	}
	public int getFragmentY() {
		return blockY >> SIZE_SHIFT;
	}
	
	public void setNext(Fragment frag) {
		nextFragment = frag;
		frag.prevFragment = this;
		hasNext = true;
	}
	
	public void remove() {
		if (hasNext)
			prevFragment.setNext(nextFragment);
		else
			prevFragment.hasNext = false;
	}
	
	
	public static int[] getIntArray() {
		return dataCache;
	}
	
	public void destroy() {
		for (int i = 0; i < images.length; i++)
			images[i].flush();
	}
	public void removeObject(MapObjectPlayer player) {
		for (int i = 0; i < objectsLength; i++) {
			if (objects[i] == player) {
				objects[i] = objects[objectsLength - 1];
				objectsLength--;
			}
		}
	}
	public BufferedImage getBufferedImage(int layer) {
		return images[layer].getImage(0);
	}
}
