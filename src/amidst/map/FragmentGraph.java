package amidst.map;

import amidst.minecraft.world.CoordinatesInWorld;

public class FragmentGraph {
	private final FragmentManager fragmentManager;
	private final Map map;

	private volatile Fragment startFragment;
	private volatile int fragmentsPerRow;
	private volatile int fragmentsPerColumn;

	public FragmentGraph(FragmentManager fragmentManager, Map map) {
		this.fragmentManager = fragmentManager;
		this.map = map;
	}

	public Fragment getStartFragment() {
		if (startFragment == null) {
			map.safeCenterOn(CoordinatesInWorld.origin());
		}
		return startFragment;
	}

	public void initStartFragment(int newLeft, int newAbove, int newRight,
			int newBelow) {
		startFragment = getStartFragment().adjustRowsAndColumns(newAbove,
				newBelow, newLeft, newRight, fragmentManager);
		fragmentsPerRow = fragmentsPerRow + newLeft + newRight;
		fragmentsPerColumn = fragmentsPerColumn + newAbove + newBelow;
	}

	public void initStartFragment(CoordinatesInWorld coordinates) {
		recycleAll();
		startFragment = fragmentManager.requestFragment(coordinates
				.toFragmentCorner());
		fragmentsPerRow = 1;
		fragmentsPerColumn = 1;
	}

	public void recycleAll() {
		if (startFragment != null) {
			startFragment.recycleAll(fragmentManager);
		}
	}

	public int getFragmentsPerRow() {
		return fragmentsPerRow;
	}

	public int getFragmentsPerColumn() {
		return fragmentsPerColumn;
	}
}
