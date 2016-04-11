package amidst.fragment;

import java.util.Iterator;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;

@NotThreadSafe
public class FragmentGraphItem implements Iterable<FragmentGraphItem> {
	/**
	 * This is an Iterator that is fail safe in the sense that it will never
	 * throw a NullPointerException or ConcurrentModificationException when the
	 * fragment graph is altered while the iterator is used. However, the
	 * elements returned by this iterator will fit the old state or the new
	 * state or something in between. This should be good enough for our use
	 * cases.
	 */
	@NotThreadSafe
	private static class FragmentGraphItemIterator implements Iterator<FragmentGraphItem> {
		private FragmentGraphItem rowStart;
		private FragmentGraphItem currentNode;

		public FragmentGraphItemIterator(FragmentGraphItem fragment) {
			rowStart = fragment.getFirstColumn().getFirstRow();
			currentNode = rowStart;
		}

		@Override
		public boolean hasNext() {
			return currentNode != null;
		}

		@Override
		public FragmentGraphItem next() {
			FragmentGraphItem result = currentNode;
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

	private final Fragment fragment;

	private volatile FragmentGraphItem leftFragment = null;
	private volatile FragmentGraphItem rightFragment = null;
	private volatile FragmentGraphItem aboveFragment = null;
	private volatile FragmentGraphItem belowFragment = null;

	@CalledOnlyBy(AmidstThread.EDT)
	public FragmentGraphItem(Fragment fragment) {
		this.fragment = fragment;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public Fragment getFragment() {
		return fragment;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	public Iterator<FragmentGraphItem> iterator() {
		return new FragmentGraphItemIterator(this);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public boolean isEndOfLine() {
		return rightFragment == null;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void recycleAll(FragmentManager manager) {
		FragmentGraphItem topLeft = getFirstColumn().getFirstRow();
		while (topLeft != null) {
			FragmentGraphItem next = topLeft.belowFragment;
			topLeft.deleteFirstRow(manager);
			topLeft = next;
		}
	}

	/**
	 * Returns the new fragment in the top left corner, but never null.
	 */
	@CalledOnlyBy(AmidstThread.EDT)
	public FragmentGraphItem adjustRowsAndColumns(
			int newAbove,
			int newBelow,
			int newLeft,
			int newRight,
			FragmentManager manager) {
		FragmentGraphItem firstColumn = getFirstColumn();
		FragmentGraphItem topLeft = firstColumn.getFirstRow();
		topLeft = topLeft.createOrRemoveRowsAbove(manager, newAbove);
		topLeft.getLastRow().createOrRemoveRowsBelow(manager, newBelow);
		topLeft = topLeft.createOrRemoveColumnsLeft(manager, newLeft);
		topLeft.getLastColumn().createOrRemoveColumnsRight(manager, newRight);
		return topLeft;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private FragmentGraphItem createOrRemoveRowsAbove(FragmentManager manager, int newAbove) {
		FragmentGraphItem topLeft = this;
		for (int i = 0; i < newAbove; i++) {
			topLeft.createFirstRow(manager);
			topLeft = topLeft.aboveFragment;
		}
		for (int i = 0; i < -newAbove; i++) {
			FragmentGraphItem next = topLeft.belowFragment;
			topLeft.deleteFirstRow(manager);
			topLeft = next;
		}
		return topLeft;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private FragmentGraphItem createOrRemoveRowsBelow(FragmentManager manager, int newBelow) {
		FragmentGraphItem bottomLeft = this;
		for (int i = 0; i < newBelow; i++) {
			bottomLeft.createLastRow(manager);
			bottomLeft = bottomLeft.belowFragment;
		}
		for (int i = 0; i < -newBelow; i++) {
			FragmentGraphItem next = bottomLeft.aboveFragment;
			bottomLeft.deleteLastRow(manager);
			bottomLeft = next;
		}
		return bottomLeft;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private FragmentGraphItem createOrRemoveColumnsLeft(FragmentManager manager, int newLeft) {
		FragmentGraphItem topLeft = this;
		for (int i = 0; i < newLeft; i++) {
			topLeft.createFirstColumn(manager);
			topLeft = topLeft.leftFragment;
		}
		for (int i = 0; i < -newLeft; i++) {
			FragmentGraphItem next = topLeft.rightFragment;
			topLeft.deleteFirstColumn(manager);
			topLeft = next;
		}
		return topLeft;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private FragmentGraphItem createOrRemoveColumnsRight(FragmentManager manager, int newRight) {
		FragmentGraphItem topRight = this;
		for (int i = 0; i < newRight; i++) {
			topRight.createLastColumn(manager);
			topRight = topRight.rightFragment;
		}
		for (int i = 0; i < -newRight; i++) {
			FragmentGraphItem next = topRight.leftFragment;
			topRight.deleteLastColumn(manager);
			topRight = next;
		}
		return topRight;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void createFirstRow(FragmentManager manager) {
		FragmentGraphItem above = createAbove(manager);
		FragmentGraphItem below = rightFragment;
		while (below != null) {
			above = above.createRight(manager);
			above.connectBelow(below);
			below = below.rightFragment;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void createLastRow(FragmentManager manager) {
		FragmentGraphItem below = createBelow(manager);
		FragmentGraphItem above = rightFragment;
		while (above != null) {
			below = below.createRight(manager);
			below.connectAbove(above);
			above = above.rightFragment;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void createFirstColumn(FragmentManager manager) {
		FragmentGraphItem left = createLeft(manager);
		FragmentGraphItem right = belowFragment;
		while (right != null) {
			left = left.createBelow(manager);
			left.connectRight(right);
			right = right.belowFragment;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void createLastColumn(FragmentManager manager) {
		FragmentGraphItem right = createRight(manager);
		FragmentGraphItem left = belowFragment;
		while (left != null) {
			right = right.createBelow(manager);
			right.connectLeft(left);
			left = left.belowFragment;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void deleteFirstRow(FragmentManager manager) {
		FragmentGraphItem current = this;
		while (current != null) {
			current.disconnectBelow();
			FragmentGraphItem right = current.disconnectRight();
			current.recycle(manager);
			current = right;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void deleteLastRow(FragmentManager manager) {
		FragmentGraphItem current = this;
		while (current != null) {
			current.disconnectAbove();
			FragmentGraphItem right = current.disconnectRight();
			current.recycle(manager);
			current = right;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void deleteFirstColumn(FragmentManager manager) {
		FragmentGraphItem current = this;
		while (current != null) {
			current.disconnectRight();
			FragmentGraphItem below = current.disconnectBelow();
			current.recycle(manager);
			current = below;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void deleteLastColumn(FragmentManager manager) {
		FragmentGraphItem current = this;
		while (current != null) {
			current.disconnectLeft();
			FragmentGraphItem below = current.disconnectBelow();
			current.recycle(manager);
			current = below;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private FragmentGraphItem getFirstRow() {
		FragmentGraphItem result = this;
		while (result.aboveFragment != null) {
			result = result.aboveFragment;
		}
		return result;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private FragmentGraphItem getLastRow() {
		FragmentGraphItem result = this;
		while (result.belowFragment != null) {
			result = result.belowFragment;
		}
		return result;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private FragmentGraphItem getFirstColumn() {
		FragmentGraphItem result = this;
		while (result.leftFragment != null) {
			result = result.leftFragment;
		}
		return result;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private FragmentGraphItem getLastColumn() {
		FragmentGraphItem result = this;
		while (result.rightFragment != null) {
			result = result.rightFragment;
		}
		return result;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private FragmentGraphItem connectAbove(FragmentGraphItem above) {
		above.belowFragment = this;
		aboveFragment = above;
		return above;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private FragmentGraphItem connectBelow(FragmentGraphItem below) {
		below.aboveFragment = this;
		belowFragment = below;
		return below;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private FragmentGraphItem connectLeft(FragmentGraphItem left) {
		left.rightFragment = this;
		leftFragment = left;
		return left;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private FragmentGraphItem connectRight(FragmentGraphItem right) {
		right.leftFragment = this;
		rightFragment = right;
		return right;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private FragmentGraphItem disconnectAbove() {
		if (aboveFragment != null) {
			aboveFragment.belowFragment = null;
		}
		FragmentGraphItem result = aboveFragment;
		aboveFragment = null;
		return result;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private FragmentGraphItem disconnectBelow() {
		if (belowFragment != null) {
			belowFragment.aboveFragment = null;
		}
		FragmentGraphItem result = belowFragment;
		belowFragment = null;
		return result;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private FragmentGraphItem disconnectLeft() {
		if (leftFragment != null) {
			leftFragment.rightFragment = null;
		}
		FragmentGraphItem result = leftFragment;
		leftFragment = null;
		return result;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private FragmentGraphItem disconnectRight() {
		if (rightFragment != null) {
			rightFragment.leftFragment = null;
		}
		FragmentGraphItem result = rightFragment;
		rightFragment = null;
		return result;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private FragmentGraphItem createAbove(FragmentManager manager) {
		return connectAbove(createFragmentGraphItem(manager, 0, -Fragment.SIZE));
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private FragmentGraphItem createBelow(FragmentManager manager) {
		return connectBelow(createFragmentGraphItem(manager, 0, Fragment.SIZE));
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private FragmentGraphItem createLeft(FragmentManager manager) {
		return connectLeft(createFragmentGraphItem(manager, -Fragment.SIZE, 0));
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private FragmentGraphItem createRight(FragmentManager manager) {
		return connectRight(createFragmentGraphItem(manager, Fragment.SIZE, 0));
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private FragmentGraphItem createFragmentGraphItem(FragmentManager manager, int xInWorld, int yInWorld) {
		return new FragmentGraphItem(manager.requestFragment(fragment.getCorner().add(xInWorld, yInWorld)));
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void recycle(FragmentManager manager) {
		manager.recycleFragment(fragment);
	}
}
