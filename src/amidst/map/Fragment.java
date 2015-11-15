package amidst.map;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import amidst.Options;
import amidst.logging.Log;
import amidst.minecraft.MinecraftUtil;

public class Fragment {
	public static final int SIZE = 512;
	public static final int SIZE_SHIFT = 9;
	public static final int MAX_OBJECTS_PER_FRAGMENT = 32;
	public static final int BIOME_SIZE = SIZE >> 2;

	// TODO: what is this? move it to another place?
	private static int[] imageRGBDataCache = new int[SIZE * SIZE];

	public static int[] getImageRGBDataCache() {
		return imageRGBDataCache;
	}

	private int blockX;
	private int blockY;

	private short[] biomeData = new short[BIOME_SIZE * BIOME_SIZE];

	private ImageLayer[] imageLayers;
	private LiveLayer[] liveLayers;
	private IconLayer[] iconLayers;

	private BufferedImage[] images;
	private MapObject[] objects;
	private int objectsLength = 0;

	private Object loadLock = new Object();

	private float alpha = 0.0f;

	private boolean isActive = false;
	private boolean isLoaded = false;

	private Fragment nextFragment = null;
	private Fragment previousFragment = null;
	private boolean hasNext = false;

	private boolean endOfLine = false;

	public Fragment(ImageLayer... layers) {
		this(layers, null, null);
	}

	public Fragment(LayerContainer layerContainer) {
		this(layerContainer.getImageLayers(), layerContainer.getLiveLayers(),
				layerContainer.getIconLayers());
	}

	private Fragment(ImageLayer[] imageLayers, LiveLayer[] liveLayers,
			IconLayer[] iconLayers) {
		this.imageLayers = imageLayers;
		this.liveLayers = liveLayers;
		this.iconLayers = iconLayers;
		initImages();
		initObjects();
	}

	private void initImages() {
		this.images = new BufferedImage[imageLayers.length];
		for (ImageLayer imageLayer : imageLayers) {
			int layerId = imageLayer.getLayerId();
			int layerSize = imageLayer.getSize();
			images[layerId] = new BufferedImage(layerSize, layerSize,
					BufferedImage.TYPE_INT_ARGB);
		}
	}

	private void initObjects() {
		this.objects = new MapObject[MAX_OBJECTS_PER_FRAGMENT];
	}

	public void load() {
		synchronized (loadLock) {
			if (isLoaded) {
				Log.w("This should never happen!");
			}
			int[] data = MinecraftUtil.getBiomeData(blockX >> 2, blockY >> 2,
					BIOME_SIZE, BIOME_SIZE, true);
			for (int i = 0; i < BIOME_SIZE * BIOME_SIZE; i++) {
				biomeData[i] = (short) data[i];
			}
			for (int i = 0; i < imageLayers.length; i++) {
				imageLayers[i].load(this);
			}
			for (int i = 0; i < iconLayers.length; i++) {
				iconLayers[i].generateMapObjects(this);
			}
			alpha = Options.instance.mapFading.get() ? 0.0f : 1.0f;
			isLoaded = true;
		}
	}

	public void drawLiveLayers(float time, Graphics2D g, AffineTransform mat) {
		for (LiveLayer liveLayer : liveLayers) {
			if (liveLayer.isVisible()) {
				liveLayer.drawLive(this, g, mat);
			}
		}
	}

	public void drawImageLayers(float time, Graphics2D g, AffineTransform mat) {
		if (!isLoaded) {
			return;
		}

		alpha = Math.min(1.0f, time * 3.0f + alpha);
		for (int i = 0; i < images.length; i++) {
			if (imageLayers[i].isVisible()) {
				setAlphaComposite(g, alpha * imageLayers[i].getAlpha());

				// TOOD: FIX THIS
				g.setTransform(imageLayers[i].getScaledMatrix(mat));
				if (g.getTransform().getScaleX() < 1.0f) {
					g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
							RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				} else {
					g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
							RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				}
				g.drawImage(images[i], 0, 0, null);
			}
		}
		setAlphaComposite(g, 1.0f);
	}

	public void drawObjects(Graphics2D g, AffineTransform mat) {
		if (alpha != 1.0f) {
			setAlphaComposite(g, alpha);
		}
		for (int i = 0; i < objectsLength; i++) {
			drawObject(g, mat, objects[i]);
		}
		if (alpha != 1.0f) {
			setAlphaComposite(g, 1.0f);
		}
	}

	private void drawObject(Graphics2D g, AffineTransform mat,
			MapObject mapObject) {
		if (mapObject.parentLayer.isVisible()) {
			double invZoom = 1.0 / mapObject.parentLayer.getMap().getZoom();
			int width = mapObject.getWidth();
			int height = mapObject.getHeight();
			g.setTransform(mat);
			g.translate(mapObject.x, mapObject.y);
			g.scale(invZoom, invZoom);
			g.drawImage(mapObject.getImage(), -(width >> 1), -(height >> 1),
					width, height, null);
		}
	}

	private void setAlphaComposite(Graphics2D g, float alpha) {
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				alpha));
	}

	public void addObject(MapObject object) {
		object.rx = object.x + this.blockX;
		object.ry = object.y + this.blockY;
		if (objectsLength >= objects.length) {
			MapObject[] tempObjects = new MapObject[objects.length << 1];
			for (int i = 0; i < objects.length; i++) {
				tempObjects[i] = objects[i];
			}
			objects = tempObjects;
		}
		objects[objectsLength] = object;
		objectsLength++;
	}

	public void removeObject(MapObjectPlayer player) {
		for (int i = 0; i < objectsLength; i++) {
			if (objects[i] == player) {
				objects[i] = objects[objectsLength - 1];
				objectsLength--;
			}
		}
	}

	public void setImageRGB(int layerId, int[] rgbArray) {
		int layerSize = imageLayers[layerId].getSize();
		images[layerId].setRGB(0, 0, layerSize, layerSize, rgbArray, 0,
				layerSize);
	}

	public void setNext(Fragment fragment) {
		nextFragment = fragment;
		fragment.previousFragment = this;
		hasNext = true;
	}

	public void remove() {
		if (hasNext) {
			previousFragment.setNext(nextFragment);
		} else {
			previousFragment.hasNext = false;
		}
	}

	public void repaintAllImageLayers() {
		synchronized (loadLock) {
			if (isLoaded) {
				for (int i = 0; i < imageLayers.length; i++) {
					imageLayers[i].load(this);
				}
			}
		}
	}

	public void repaintImageLayer(int layerId) {
		synchronized (loadLock) {
			if (isLoaded) {
				imageLayers[layerId].load(this);
			}
		}
	}

	public void recycle() {
		isActive = false;
		isLoaded = false;
	}

	public void init(int x, int y) {
		for (IconLayer layer : iconLayers) {
			layer.clearMapObjects(this);
		}
		hasNext = false;
		endOfLine = false;
		blockX = x;
		blockY = y;
		isActive = true;
	}

	public void reset() {
		objectsLength = 0;
		isActive = false;
		isLoaded = false;

		nextFragment = null;
		previousFragment = null;
		hasNext = false;

		endOfLine = false;
	}

	public boolean needsLoading() {
		return isActive && !isLoaded;
	}

	public Fragment getNext() {
		return nextFragment;
	}

	public Fragment getPrevious() {
		return previousFragment;
	}

	public boolean hasNext() {
		return hasNext;
	}

	public boolean isEndOfLine() {
		return endOfLine;
	}

	public void setEndOfLine(boolean endOfLine) {
		this.endOfLine = endOfLine;
	}

	public short[] getBiomeData() {
		return biomeData;
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

	public MapObject[] getObjects() {
		return objects;
	}

	public int getObjectsLength() {
		return objectsLength;
	}

	public void setObjectsLength(int objectsLength) {
		this.objectsLength = objectsLength;
	}
}
