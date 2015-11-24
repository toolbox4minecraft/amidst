package amidst.map;

import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import amidst.Options;
import amidst.map.layer.IconLayer;
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

	private BufferedImage[] images;
	private boolean[] repaintImage;
	private List<IconLayer> invalidatedIconLayers = new LinkedList<IconLayer>();
	private List<MapObject> mapObjects = new LinkedList<MapObject>();
	private float alpha = 0.0f;

	private boolean isLoaded = false;
	private CoordinatesInWorld corner;
	private Fragment leftFragment = null;
	private Fragment rightFragment = null;
	private Fragment aboveFragment = null;
	private Fragment belowFragment = null;

	public Fragment(BufferedImage[] images, boolean[] repaintImage) {
		this.images = images;
		this.repaintImage = repaintImage;
	}

	public void initialize(CoordinatesInWorld corner) {
		unload();
		this.corner = corner;
	}

	private void unload() {
		isLoaded = false;
		leftFragment = null;
		rightFragment = null;
		aboveFragment = null;
		belowFragment = null;
	}

	public void reset() {
		this.corner = null;
	}

	public boolean isInitialized() {
		return corner != null;
	}

	public boolean isLoaded() {
		return isLoaded;
	}

	public void setLoaded() {
		isLoaded = true;
	}

	public void clearMapObjects() {
		mapObjects.clear();
	}

	public void clearInvalidatedIconLayers() {
		invalidatedIconLayers.clear();
	}

	public void initAlpha() {
		alpha = Options.instance.mapFading.get() ? 0.0f : 1.0f;
	}

	public void updateAlpha(float time) {
		alpha = Math.min(1.0f, time * 3.0f + alpha);
	}

	public void addObject(MapObject mapObject) {
		mapObjects.add(mapObject);
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

	public float getAlpha() {
		return alpha;
	}

	public List<MapObject> getMapObjects() {
		return mapObjects;
	}

	public BufferedImage[] getImages() {
		return images;
	}

	public boolean needsImageRepaint(int layerId) {
		return repaintImage[layerId];
	}

	public void setNeedsImageRepaint(int layerId, boolean value) {
		repaintImage[layerId] = value;
	}

	public List<IconLayer> getInvalidatedIconLayers() {
		return invalidatedIconLayers;
	}

	public BufferedImage getImage(int layerId) {
		return images[layerId];
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
