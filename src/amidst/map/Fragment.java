package amidst.map;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import amidst.Options;
import amidst.logging.Log;
import amidst.map.layer.IconLayer;
import amidst.map.layer.ImageLayer;
import amidst.map.layer.LiveLayer;
import amidst.map.object.MapObject;
import amidst.minecraft.MinecraftUtil;

public class Fragment implements Iterable<Fragment> {
	private static class FragmentIterator implements Iterator<Fragment> {
		private Fragment rowStart;
		private Fragment currentNode;

		public FragmentIterator(Fragment fragment) {
			rowStart = fragment.getFirstColumn().getFirstRow();
			currentNode = rowStart;
		}

		@Override
		public boolean hasNext() {
			return currentNode != null;
		}

		@Override
		public Fragment next() {
			Fragment result = currentNode;
			updateCurrentNode();
			return result;
		}

		private void updateCurrentNode() {
			if (currentNode.isEndOfLine()) {
				rowStart = rowStart.belowFragment;
				currentNode = rowStart;
			} else {
				currentNode = currentNode.rightFragment;
			}
		}
	}

	public static final int SIZE = 512;
	public static final int SIZE_SHIFT = 9;
	public static final int INITIAL_NUMBER_OF_OBJECTS_PER_FRAGMENT = 32;
	public static final int BIOME_SIZE = SIZE >> 2;

	// TODO: what is this? move it to another place?
	private static int[] imageRGBDataCache = new int[SIZE * SIZE];

	public static int[] getImageRGBDataCache() {
		return imageRGBDataCache;
	}

	private Object loadLock = new Object();

	private ImageLayer[] imageLayers;
	private LiveLayer[] liveLayers;
	private IconLayer[] iconLayers;
	private BufferedImage[] images;

	private short[] biomeData = new short[BIOME_SIZE * BIOME_SIZE];

	private boolean isInitialized = false;
	private boolean isLoaded = false;
	private Set<MapObject> mapObjects = new HashSet<MapObject>();
	private int xInWorld;
	private int yInWorld;
	private float alpha = 0.0f;
	private Fragment leftFragment = null;
	private Fragment rightFragment = null;
	private Fragment aboveFragment = null;
	private Fragment belowFragment = null;

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

	public void load() {
		synchronized (loadLock) {
			if (isLoaded) {
				Log.w("This should never happen!");
			}
			int[] data = MinecraftUtil.getBiomeData(xInWorld >> 2,
					yInWorld >> 2, BIOME_SIZE, BIOME_SIZE, true);
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

				// TODO: FIX THIS
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

	public void drawObjects(Graphics2D g, AffineTransform mat, Map map) {
		if (alpha != 1.0f) {
			setAlphaComposite(g, alpha);
		}
		for (MapObject mapObject : mapObjects) {
			drawObject(g, mat, mapObject, map);
		}
		if (alpha != 1.0f) {
			setAlphaComposite(g, 1.0f);
		}
	}

	private void drawObject(Graphics2D g, AffineTransform mat,
			MapObject mapObject, Map map) {
		if (mapObject.isVisible()) {
			double invZoom = 1.0 / map.getZoom();
			int width = mapObject.getWidth();
			int height = mapObject.getHeight();
			if (map.getSelectedMapObject() == mapObject) {
				width *= 1.5;
				height *= 1.5;
			}
			g.setTransform(mat);
			g.translate(mapObject.getXInFragment(), mapObject.getYInFragment());
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
		mapObjects.add(object);
	}

	public void removeObject(MapObject mapObject) {
		mapObjects.remove(mapObject);
	}

	public void setImageRGB(int layerId, int[] rgbArray) {
		int layerSize = imageLayers[layerId].getSize();
		images[layerId].setRGB(0, 0, layerSize, layerSize, rgbArray, 0,
				layerSize);
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

	public boolean isInBounds(MapObject mapObject) {
		return isInBounds(mapObject.getXInWorld(), mapObject.getYInWorld());
	}

	private boolean isInBounds(int xInWorld, int yInWorld) {
		return xInWorld >= this.xInWorld
				&& xInWorld < this.xInWorld + Fragment.SIZE
				&& yInWorld >= this.yInWorld
				&& yInWorld < this.yInWorld + Fragment.SIZE;
	}

	public Fragment recycleAll(FragmentManager manager) {
		Fragment topLeft = getFirstColumn().getFirstRow();
		while (topLeft != null) {
			Fragment next = topLeft.belowFragment;
			topLeft.deleteFirstRow(manager);
			topLeft = next;
		}
		return topLeft;
	}

	public Fragment adjustRowsAndColumns(int newAbove, int newBelow,
			int newLeft, int newRight, FragmentManager manager) {
		Fragment firstColumn = getFirstColumn();
		Fragment topLeft = firstColumn.getFirstRow();
		topLeft = topLeft.createOrRemoveRowsAbove(manager, newAbove);
		topLeft = topLeft.createOrRemoveColumnsLeft(manager, newLeft);
		topLeft.getLastColumn().createOrRemoveColumnsRight(manager, newRight);
		firstColumn.getLastRow().createOrRemoveRowsBelow(manager, newBelow);
		return topLeft;
	}

	private Fragment createOrRemoveRowsAbove(FragmentManager manager,
			int newAbove) {
		Fragment topLeft = this;
		for (int i = 0; i < newAbove; i++) {
			topLeft.createFirstRow(manager);
			topLeft = topLeft.aboveFragment;
		}
		for (int i = 0; i < -newAbove; i++) {
			Fragment next = topLeft.belowFragment;
			topLeft.deleteFirstRow(manager);
			topLeft = next;
		}
		return topLeft;
	}

	private Fragment createOrRemoveRowsBelow(FragmentManager manager,
			int newBelow) {
		Fragment bottomLeft = this;
		for (int i = 0; i < newBelow; i++) {
			bottomLeft.createLastRow(manager);
			bottomLeft = bottomLeft.belowFragment;
		}
		for (int i = 0; i < -newBelow; i++) {
			Fragment next = bottomLeft.aboveFragment;
			bottomLeft.deleteLastRow(manager);
			bottomLeft = next;
		}
		return bottomLeft;
	}

	private Fragment createOrRemoveColumnsLeft(FragmentManager manager,
			int newLeft) {
		Fragment topLeft = this;
		for (int i = 0; i < newLeft; i++) {
			topLeft.createFirstColumn(manager);
			topLeft = topLeft.leftFragment;
		}
		for (int i = 0; i < -newLeft; i++) {
			Fragment next = topLeft.rightFragment;
			topLeft.deleteFirstColumn(manager);
			topLeft = next;
		}
		return topLeft;
	}

	private Fragment createOrRemoveColumnsRight(FragmentManager manager,
			int newRight) {
		Fragment topRight = this;
		for (int i = 0; i < newRight; i++) {
			topRight.createLastColumn(manager);
			topRight = topRight.rightFragment;
		}
		for (int i = 0; i < -newRight; i++) {
			Fragment next = topRight.leftFragment;
			topRight.deleteLastColumn(manager);
			topRight = next;
		}
		return topRight;
	}

	private void createFirstRow(FragmentManager manager) {
		Fragment above = createAbove(manager);
		Fragment below = rightFragment;
		while (below != null) {
			above = above.createRight(manager);
			above.connectBelow(below);
			below = below.rightFragment;
		}
	}

	private void createLastRow(FragmentManager manager) {
		Fragment below = createBelow(manager);
		Fragment above = rightFragment;
		while (above != null) {
			below = below.createRight(manager);
			below.connectAbove(above);
			above = above.rightFragment;
		}
	}

	private void createFirstColumn(FragmentManager manager) {
		Fragment left = createLeft(manager);
		Fragment right = belowFragment;
		while (right != null) {
			left = left.createBelow(manager);
			left.connectRight(right);
			right = right.belowFragment;
		}
	}

	private void createLastColumn(FragmentManager manager) {
		Fragment right = createRight(manager);
		Fragment left = belowFragment;
		while (left != null) {
			right = right.createBelow(manager);
			right.connectLeft(left);
			left = left.belowFragment;
		}
	}

	private void deleteFirstRow(FragmentManager manager) {
		Fragment current = this;
		while (current != null) {
			current.disconnectBelow();
			Fragment right = current.disconnectRight();
			current.recycle(manager);
			current = right;
		}
	}

	private void deleteLastRow(FragmentManager manager) {
		Fragment current = this;
		while (current != null) {
			current.disconnectAbove();
			Fragment right = current.disconnectRight();
			current.recycle(manager);
			current = right;
		}
	}

	private void deleteFirstColumn(FragmentManager manager) {
		Fragment current = this;
		while (current != null) {
			current.disconnectRight();
			Fragment below = current.disconnectBelow();
			current.recycle(manager);
			current = below;
		}
	}

	private void deleteLastColumn(FragmentManager manager) {
		Fragment current = this;
		while (current != null) {
			current.disconnectLeft();
			Fragment below = current.disconnectBelow();
			current.recycle(manager);
			current = below;
		}
	}

	private Fragment getFirstRow() {
		Fragment result = this;
		while (result.aboveFragment != null) {
			result = result.aboveFragment;
		}
		return result;
	}

	private Fragment getLastRow() {
		Fragment result = this;
		while (result.belowFragment != null) {
			result = result.belowFragment;
		}
		return result;
	}

	private Fragment getFirstColumn() {
		Fragment result = this;
		while (result.leftFragment != null) {
			result = result.leftFragment;
		}
		return result;
	}

	private Fragment getLastColumn() {
		Fragment result = this;
		while (result.rightFragment != null) {
			result = result.rightFragment;
		}
		return result;
	}

	private Fragment connectAbove(Fragment above) {
		above.belowFragment = this;
		aboveFragment = above;
		return above;
	}

	private Fragment connectBelow(Fragment below) {
		below.aboveFragment = this;
		belowFragment = below;
		return below;
	}

	private Fragment connectLeft(Fragment left) {
		left.rightFragment = this;
		leftFragment = left;
		return left;
	}

	private Fragment connectRight(Fragment right) {
		right.leftFragment = this;
		rightFragment = right;
		return right;
	}

	private Fragment disconnectAbove() {
		if (aboveFragment != null) {
			aboveFragment.belowFragment = null;
		}
		Fragment result = aboveFragment;
		aboveFragment = null;
		return result;
	}

	private Fragment disconnectBelow() {
		if (belowFragment != null) {
			belowFragment.aboveFragment = null;
		}
		Fragment result = belowFragment;
		belowFragment = null;
		return result;
	}

	private Fragment disconnectLeft() {
		if (leftFragment != null) {
			leftFragment.rightFragment = null;
		}
		Fragment result = leftFragment;
		leftFragment = null;
		return result;
	}

	private Fragment disconnectRight() {
		if (rightFragment != null) {
			rightFragment.leftFragment = null;
		}
		Fragment result = rightFragment;
		rightFragment = null;
		return result;
	}

	private Fragment createAbove(FragmentManager manager) {
		return connectAbove(manager.requestFragment(xInWorld, yInWorld - SIZE));
	}

	private Fragment createBelow(FragmentManager manager) {
		return connectBelow(manager.requestFragment(xInWorld, yInWorld + SIZE));
	}

	private Fragment createLeft(FragmentManager manager) {
		return connectLeft(manager.requestFragment(xInWorld - SIZE, yInWorld));
	}

	private Fragment createRight(FragmentManager manager) {
		return connectRight(manager.requestFragment(xInWorld + SIZE, yInWorld));
	}

	private void recycle(FragmentManager manager) {
		recycle();
		manager.recycleFragment(this);
	}

	public void initialize(int xInWorld, int yInWorld) {
		isLoaded = false;
		clearMapObjects();
		this.xInWorld = xInWorld;
		this.yInWorld = yInWorld;
		alpha = 0.0f;
		leftFragment = null;
		rightFragment = null;
		aboveFragment = null;
		belowFragment = null;
		isInitialized = true;
	}

	public void reset() {
		isInitialized = false;
		isLoaded = false;
		clearMapObjects();
		xInWorld = 0;
		yInWorld = 0;
		alpha = 0.0f;
		leftFragment = null;
		rightFragment = null;
		aboveFragment = null;
		belowFragment = null;
	}

	public void recycle() {
		isInitialized = false;
		isLoaded = false;
	}

	public boolean needsLoading() {
		return isInitialized && !isLoaded;
	}

	public short[] getBiomeData() {
		return biomeData;
	}

	public int getXInWorld() {
		return xInWorld;
	}

	public int getYInWorld() {
		return yInWorld;
	}

	public int getChunkXInWorld() {
		return xInWorld >> 4;
	}

	public int getChunkYInWorld() {
		return yInWorld >> 4;
	}

	public int getFragmentXInWorld() {
		return xInWorld >> SIZE_SHIFT;
	}

	public int getFragmentYInWorld() {
		return yInWorld >> SIZE_SHIFT;
	}

	public boolean isEndOfLine() {
		return rightFragment == null;
	}

	@Override
	public Iterator<Fragment> iterator() {
		return new FragmentIterator(this);
	}

	public Set<MapObject> getMapObjects() {
		return mapObjects;
	}

	private void clearMapObjects() {
		for (MapObject mapObject : copyMapObjectsToPreventConcurrentModificationException()) {
			mapObject.setFragment(null);
		}
	}

	@Deprecated
	private HashSet<MapObject> copyMapObjectsToPreventConcurrentModificationException() {
		return new HashSet<MapObject>(mapObjects);
	}
}
