package amidst.map;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Vector;

import amidst.Log;

public class Fragment {
	public static final int SIZE = 512, SIZE_SHIFT = 9, MAX_OBJECTS_PER_FRAGMENT = 20;
	
	public int blockX, blockY;
	
	private Layer[] layers;
	private Layer[] liveLayers;
	private IconLayer[] iconLayers;
	
	private BufferedImage[] imgBuffers;
	public MapObject[] objects;
	public int objectsLength = 0;
	
	public boolean isActive = false;
	public boolean isLoaded = false;
	
	public Fragment nextFragment = null, prevFragment = null;
	public boolean hasNext = false;
	
	public boolean endOfLine = false;
	
	private static int[] dataCache = new int[SIZE*SIZE];
	
	public Fragment(Layer... layers) {
		this(layers, null, null);
	}
	public Fragment(Layer[] layers, Layer[] liveLayers, IconLayer[] iconLayers) {
		this.layers = layers;
		this.liveLayers = liveLayers;
		imgBuffers = new BufferedImage[layers.length];
		for (int i = 0; i < layers.length; i++) {
			if (!layers[i].isLive())
				imgBuffers[i] = new BufferedImage(layers[i].size, layers[i].size, BufferedImage.TYPE_INT_ARGB);
		}
		this.iconLayers = iconLayers;
		objects = new MapObject[MAX_OBJECTS_PER_FRAGMENT];
	}
	
	public void load() {
		if (isLoaded)
			Log.w("This should never happen!");
		for (int i = 0; i < layers.length; i++) {
			if (!layers[i].isLive())
				layers[i].draw(this, i);
		}
		for (int i = 0; i < iconLayers.length; i++) {
			iconLayers[i].generateMapObjects(this);
		}
		isLoaded = true;
	}
	
	public void recycle() {
		isActive = false;
		if (isLoaded) {
			for (Layer layer : layers)
				layer.unload(this);
		}
		
		isLoaded = false;
	}
	
	public void clear() {
		objectsLength = 0;
		//isLoaded = false;
		hasNext = false;
		endOfLine = false;
		isActive = true;
		for (int i = 0; i < imgBuffers.length; i++) {
			if (!layers[i].isLive())
				imgBuffers[i].setRGB(0, 0, layers[i].size, layers[i].size, layers[i].getDefaultData(), 0, layers[i].size);
		}
	}
	public void drawLive(Graphics2D g, AffineTransform mat) {
		for (int i = 0; i < liveLayers.length; i++) {
			if (liveLayers[i].isVisible()) {
				liveLayers[i].drawLive(this, g, liveLayers[i].getMatrix(mat));
			}
		}
		
	}
	public void draw(Graphics2D g, AffineTransform mat) {
		for (int i = 0; i < imgBuffers.length; i++) {
			if (layers[i].isVisible()) {
				if (layers[i].isLive()) {
					layers[i].drawLive(this, g, layers[i].getMatrix(mat));
				} else {
					g.setTransform(layers[i].getScaledMatrix(mat));
					g.drawImage(imgBuffers[i], 0, 0, null);
				}
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
		Layer layer = layers[layerID];
		imgBuffers[layerID].setRGB(0, 0, layer.size, layer.size, data, 0, layer.size);
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
	
	public Graphics2D getDraw(int layerID) {
		Graphics2D g2d =  imgBuffers[layerID].createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		return g2d;
	}
	
	public static int[] getIntArray() {
		return dataCache;
	}
	
	public BufferedImage getBufferedImage(int layerID) {
		return imgBuffers[layerID];
	}
	public void destroy() {
		for (int i = 0; i < imgBuffers.length; i++)
			imgBuffers[i].flush();
	}
}
