package amidst.map;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;

import amidst.Options;
import amidst.minecraft.world.BiomeDataOracle;
import amidst.minecraft.world.CoordinatesInWorld;
import amidst.minecraft.world.Resolution;
import amidst.minecraft.world.icon.WorldIcon;

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
			currentNode = currentNode.rightFragment;
			if (currentNode == null) {
				rowStart = rowStart.belowFragment;
				currentNode = rowStart;
			}
		}
	}

	public static final int SIZE = Resolution.FRAGMENT.getStep();

	private volatile boolean isInitialized = false;
	private volatile boolean isLoaded = false;
	private volatile CoordinatesInWorld corner;
	private volatile Fragment leftFragment = null;
	private volatile Fragment rightFragment = null;
	private volatile Fragment aboveFragment = null;
	private volatile Fragment belowFragment = null;

	private volatile float alpha;
	private volatile short[][] biomeData;

	private final AtomicReferenceArray<BufferedImage> images;
	private final AtomicReferenceArray<List<WorldIcon>> worldIcons;

	public Fragment(int numberOfLayers) {
		this.images = new AtomicReferenceArray<BufferedImage>(numberOfLayers);
		this.worldIcons = new AtomicReferenceArray<List<WorldIcon>>(
				numberOfLayers);
	}

	public void initialize(CoordinatesInWorld corner) {
		this.corner = corner;
		leftFragment = null;
		rightFragment = null;
		aboveFragment = null;
		belowFragment = null;
	}

	public void prepareLoad() {
		initAlpha();
	}

	public void prepareReload() {
	}

	public void prepareDraw(float time) {
		updateAlpha(time);
	}

	private void initAlpha() {
		alpha = Options.instance.mapFading.get() ? 0.0f : 1.0f;
	}

	private void updateAlpha(float time) {
		alpha = Math.min(1.0f, time * 3.0f + alpha);
	}

	public float getAlpha() {
		return alpha;
	}

	public void initBiomeData(int width, int height) {
		biomeData = new short[width][height];
	}

	public void populateBiomeData(BiomeDataOracle biomeDataOracle) {
		biomeDataOracle.populateArrayUsingQuarterResolution(corner, biomeData);
	}

	public String getBiomeAliasAt(CoordinatesInWorld coordinates,
			String defaultAlias) {
		if (!isLoaded) {
			return defaultAlias;
		}
		long x = coordinates.getXRelativeToFragmentAs(Resolution.QUARTER);
		long y = coordinates.getYRelativeToFragmentAs(Resolution.QUARTER);
		short biome = getBiomeDataAt((int) x, (int) y);
		return Options.instance.biomeColorProfile.getAliasForId(biome);
	}

	public short getBiomeDataAt(int x, int y) {
		return biomeData[x][y];
	}

	public BufferedImage getAndSetImage(int layerId, BufferedImage image) {
		return images.getAndSet(layerId, image);
	}

	public void putImage(int layerId, BufferedImage image) {
		images.set(layerId, image);
	}

	public BufferedImage getImage(int layerId) {
		return images.get(layerId);
	}

	public void putWorldIcons(int layerId, List<WorldIcon> icons) {
		worldIcons.set(layerId, icons);
	}

	public List<WorldIcon> getWorldIcons(int layerId) {
		if (isLoaded) {
			List<WorldIcon> result = worldIcons.get(layerId);
			if (result != null) {
				return result;
			}
		}
		return Collections.emptyList();
	}

	public void setInitialized(boolean isInitialized) {
		this.isInitialized = isInitialized;
	}

	public boolean isInitialized() {
		return isInitialized;
	}

	public void setLoaded(boolean isLoaded) {
		this.isLoaded = isLoaded;
	}

	public boolean isLoaded() {
		return isLoaded;
	}

	/**
	 * Creates an Iterator that is fail safe in the sense that it will never
	 * throw a NullPointerException or ConcurrentModificationException when the
	 * fragment graph is altered while the iterator is used. However, the
	 * elements returned by this iterator will be the old state or the new state
	 * or something in between. This should be good enough for our use cases.
	 */
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

	public void recycleAll(FragmentManager manager) {
		Fragment topLeft = getFirstColumn().getFirstRow();
		while (topLeft != null) {
			Fragment next = topLeft.belowFragment;
			topLeft.deleteFirstRow(manager);
			topLeft = next;
		}
	}

	/**
	 * Returns the new fragment in the top left corner, but never null.
	 */
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
