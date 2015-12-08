package amidst.fragment;

import java.util.Iterator;
import java.util.List;

import amidst.fragment.layer.LayerDeclaration;
import amidst.minecraft.world.CoordinatesInWorld;
import amidst.minecraft.world.icon.WorldIcon;

public class FragmentGraph implements Iterable<FragmentGraphItem> {
	private final List<LayerDeclaration> declarations;
	private final FragmentManager fragmentManager;

	private volatile FragmentGraphItem topLeftFragment;
	private volatile int fragmentsPerRow;
	private volatile int fragmentsPerColumn;

	public FragmentGraph(List<LayerDeclaration> declarations,
			FragmentManager fragmentManager) {
		this.declarations = declarations;
		this.fragmentManager = fragmentManager;
	}

	private FragmentGraphItem getTopLeftFragment() {
		if (topLeftFragment == null) {
			init(CoordinatesInWorld.origin());
		}
		return topLeftFragment;
	}

	public void adjust(int newLeft, int newAbove, int newRight, int newBelow) {
		topLeftFragment = getTopLeftFragment().adjustRowsAndColumns(newAbove,
				newBelow, newLeft, newRight, fragmentManager);
		fragmentsPerRow = fragmentsPerRow + newLeft + newRight;
		fragmentsPerColumn = fragmentsPerColumn + newAbove + newBelow;
	}

	public void init(CoordinatesInWorld coordinates) {
		recycleAll();
		topLeftFragment = new FragmentGraphItem(
				fragmentManager.requestFragment(coordinates.toFragmentCorner()));
		fragmentsPerRow = 1;
		fragmentsPerColumn = 1;
	}

	public void dispose() {
		recycleAll();
	}

	private void recycleAll() {
		if (topLeftFragment != null) {
			topLeftFragment.recycleAll(fragmentManager);
		}
	}

	public int getFragmentsPerRow() {
		return fragmentsPerRow;
	}

	public int getFragmentsPerColumn() {
		return fragmentsPerColumn;
	}

	public CoordinatesInWorld getCorner() {
		return topLeftFragment.getFragment().getCorner();
	}

	@Override
	public Iterator<FragmentGraphItem> iterator() {
		return getTopLeftFragment().iterator();
	}

	public WorldIcon getClosestWorldIcon(CoordinatesInWorld coordinates,
			double maxDistanceInWorld) {
		return new ClosestWorldIconFinder(this, declarations, coordinates,
				maxDistanceInWorld).getWorldIcon();
	}

	public Fragment getFragmentAt(CoordinatesInWorld coordinates) {
		CoordinatesInWorld corner = coordinates.toFragmentCorner();
		for (FragmentGraphItem fragmentGraphItem : getTopLeftFragment()) {
			Fragment fragment = fragmentGraphItem.getFragment();
			if (corner.equals(fragment.getCorner())) {
				return fragment;
			}
		}
		return null;
	}
}
