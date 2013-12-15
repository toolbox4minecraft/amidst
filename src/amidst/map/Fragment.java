package amidst.map;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import amidst.logging.Log;
import amidst.minecraft.MinecraftUtil;

public class Fragment {
	public static final int SIZE = 512, SIZE_SHIFT = 9, MAX_OBJECTS_PER_FRAGMENT = 20, MIPMAP_LEVELS = 3, BIOME_SIZE = SIZE >> 2;
	
	public int blockX, blockY;
	
	public short[] biomeData = new short[BIOME_SIZE * BIOME_SIZE];
	
	private Layer[] layers;
	private Layer[] liveLayers;
	private IconLayer[] iconLayers;
	
	private BufferedImage[] images;
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
		images = new BufferedImage[layers.length];
		for (int i = 0; i < layers.length; i++)
			images[i] = new BufferedImage(layers[i].size, layers[i].size, BufferedImage.TYPE_INT_ARGB); //GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleVolatileImage(layers[i].size, layers[i].size);
		this.iconLayers = iconLayers;
		objects = new MapObject[MAX_OBJECTS_PER_FRAGMENT];
	}
	
	public void load() {
		if (isLoaded)
			Log.w("This should never happen!");
		int[] data = MinecraftUtil.getBiomeData(blockX >> 2, blockY >> 2, BIOME_SIZE, BIOME_SIZE);
		for (int i = 0; i < BIOME_SIZE * BIOME_SIZE; i++)
			biomeData[i] = (short)data[i];
		for (int i = 0; i < layers.length; i++)
			layers[i].load(this, i);
		for (int i = 0; i < iconLayers.length; i++)
			iconLayers[i].generateMapObjects(this);
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
	
	public void clearData() {
		for (IconLayer layer : iconLayers)
			layer.clearMapObjects(this);
		isLoaded = false;
	}
	
	public void clear() {
		for (IconLayer layer : iconLayers)
			layer.clearMapObjects(this);
		//isLoaded = false;
		hasNext = false;
		endOfLine = false;
		isActive = true;
	}
	public void drawLive(Graphics2D g, AffineTransform mat) {
		for (int i = 0; i < liveLayers.length; i++) {
			if (liveLayers[i].isVisible()) {
				liveLayers[i].drawLive(this, g, liveLayers[i].getMatrix(mat));
			}
		}
		
	}
	public void draw(Graphics2D g, AffineTransform mat) {
		if (isLoaded) {
			for (int i = 0; i < images.length; i++) {
				if (layers[i].isVisible()) {
					g.setTransform(layers[i].getScaledMatrix(mat));
					g.drawImage(images[i], 0, 0, null);
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
	
	public void setImageData(int layerId, int[] data) {
		images[layerId].setRGB(0, 0, layers[layerId].size, layers[layerId].size, data, 0, layers[layerId].size);
	}
	
	
	public int getBlockX() {
		return blockX;
	}
	public int getBlockY() {
		return blockY;
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
	
	public void removeObject(MapObjectPlayer player) {
		for (int i = 0; i < objectsLength; i++) {
			if (objects[i] == player) {
				objects[i] = objects[objectsLength - 1];
				objectsLength--;
			}
		}
	}
	public BufferedImage getBufferedImage(int layer) {
		return images[layer];
	}
	public void reset() {
		objectsLength = 0;
		isActive = false;
		isLoaded = false;
		
		nextFragment = null;
		prevFragment = null;
		hasNext = false;
		
		endOfLine = false;
	}
	public void repaint() {
		if (isLoaded)
			for (int i = 0; i < layers.length; i++)
				layers[i].load(this, i);
	}
}
