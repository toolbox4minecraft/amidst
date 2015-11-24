package amidst.map;

import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import amidst.Options;
import amidst.map.layer.IconLayer;
import amidst.map.layer.ImageLayer;
import amidst.map.layer.MapObject;
import amidst.minecraft.world.CoordinatesInWorld;

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
	@Deprecated
	public static final int BIOME_SIZE = SIZE >> 2;

	private LayerContainer layerContainer;

	private BufferedImage[] images;
	private boolean[] repaintImage;
	private List<IconLayer> invalidatedIconLayers = new LinkedList<IconLayer>();

	private boolean isLoaded = false;
	private List<MapObject> mapObjects = new LinkedList<MapObject>();
	private CoordinatesInWorld corner;
	private float alpha = 0.0f;
	private Fragment leftFragment = null;
	private Fragment rightFragment = null;
	private Fragment aboveFragment = null;
	private Fragment belowFragment = null;

	public Fragment(LayerContainer layerContainer) {
		this.layerContainer = layerContainer;
		initImages();
	}

	private void initImages() {
		ImageLayer[] imageLayers = layerContainer.getImageLayers();
		this.images = new BufferedImage[imageLayers.length];
		this.repaintImage = new boolean[imageLayers.length];
		for (ImageLayer imageLayer : imageLayers) {
			int layerId = imageLayer.getLayerId();
			int layerSize = imageLayer.getSize();
			images[layerId] = new BufferedImage(layerSize, layerSize,
					BufferedImage.TYPE_INT_ARGB);
			repaintImage[layerId] = true;
		}
	}

	public void initialize(CoordinatesInWorld corner) {
		doReset();
		this.corner = corner;
	}

	public void reset() {
		this.corner = null;
		doReset();
	}

	private void doReset() {
		isLoaded = false;
		mapObjects.clear();
		invalidatedIconLayers.clear();
		alpha = 0.0f;
		leftFragment = null;
		rightFragment = null;
		aboveFragment = null;
		belowFragment = null;
	}

	private boolean isInitialized() {
		return corner != null;
	}

	// TODO: move this to the class FragmentLoader
	@Deprecated
	public void load(int[] imageCache) {
		if (isInitialized()) {
			ImageLayer[] imageLayers = layerContainer.getImageLayers();
			if (isLoaded()) {
				repaintInvalidatedImages(imageCache, imageLayers);
				reloadInvalidatedIconLayers();
			} else {
				repaintAllImages(imageCache, imageLayers);
				generateMapObjects();
				initAlpha();
				setLoaded();
			}
		}
	}

	private void setLoaded() {
		isLoaded = true;
	}

	private void repaintInvalidatedImages(int[] imageCache,
			ImageLayer[] imageLayers) {
		for (int i = 0; i < imageLayers.length; i++) {
			if (repaintImage[i]) {
				repaintImage(i, imageCache, imageLayers);
			}
		}
	}

	private void reloadInvalidatedIconLayers() {
		for (IconLayer iconLayer : invalidatedIconLayers) {
			removeIconLayer(iconLayer);
			iconLayer.generateMapObjects(this);
		}
		invalidatedIconLayers.clear();
	}

	private void removeIconLayer(IconLayer iconLayer) {
		List<MapObject> objectsToRemove = new LinkedList<MapObject>();
		for (MapObject mapObject : mapObjects) {
			if (mapObject.getIconLayer() == iconLayer) {
				objectsToRemove.add(mapObject);
			}
		}
		mapObjects.removeAll(objectsToRemove);
	}

	private void repaintAllImages(int[] imageCache, ImageLayer[] imageLayers) {
		for (int i = 0; i < imageLayers.length; i++) {
			repaintImage(i, imageCache, imageLayers);
		}
	}

	private void repaintImage(int layerId, int[] imageCache,
			ImageLayer[] imageLayers) {
		imageLayers[layerId].drawToCache(this, imageCache, images[layerId]);
		repaintImage[layerId] = false;
	}

	private void generateMapObjects() {
		for (IconLayer iconLayer : layerContainer.getIconLayers()) {
			iconLayer.generateMapObjects(this);
		}
	}

	private void initAlpha() {
		alpha = Options.instance.mapFading.get() ? 0.0f : 1.0f;
	}

	// TODO: move this to the class FragmentLoader
	@Deprecated
	public void invalidateImageLayer(int layerId) {
		if (isLoaded) {
			repaintImage[layerId] = true;
		}
	}

	// TODO: move this to the class FragmentLoader
	@Deprecated
	public void invalidateIconLayer(IconLayer iconLayer) {
		if (isLoaded) {
			invalidatedIconLayers.add(iconLayer);
		}
	}

	public void updateAlpha(float time) {
		alpha = Math.min(1.0f, time * 3.0f + alpha);
	}

	public void addObject(MapObject mapObject) {
		mapObjects.add(mapObject);
	}

	public void removeObject(MapObject mapObject) {
		if (isLoaded) {
			mapObjects.remove(mapObject);
		}
	}

	public float getAlpha() {
		return alpha;
	}

	public List<MapObject> getMapObjects() {
		return mapObjects;
	}

	public boolean isLoaded() {
		return isLoaded;
	}

	public BufferedImage[] getImages() {
		return images;
	}

	@Override
	public Iterator<Fragment> iterator() {
		return new FragmentIterator(this);
	}

	public boolean isInBounds(CoordinatesInWorld coordinates) {
		return coordinates.isInBoundsOf(corner, SIZE);
	}

	public CoordinatesInWorld getCorner() {
		return corner;
	}

	public boolean isEndOfLine() {
		return rightFragment == null;
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
		topLeft.getLastRow().createOrRemoveRowsBelow(manager, newBelow);
		topLeft = topLeft.createOrRemoveColumnsLeft(manager, newLeft);
		topLeft.getLastColumn().createOrRemoveColumnsRight(manager, newRight);
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
		return connectAbove(manager.requestFragment(corner.add(0, -SIZE)));
	}

	private Fragment createBelow(FragmentManager manager) {
		return connectBelow(manager.requestFragment(corner.add(0, SIZE)));
	}

	private Fragment createLeft(FragmentManager manager) {
		return connectLeft(manager.requestFragment(corner.add(-SIZE, 0)));
	}

	private Fragment createRight(FragmentManager manager) {
		return connectRight(manager.requestFragment(corner.add(SIZE, 0)));
	}

	private void recycle(FragmentManager manager) {
		manager.recycleFragment(this);
	}
}
