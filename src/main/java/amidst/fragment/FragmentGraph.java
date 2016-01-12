package amidst.fragment;

import java.util.Iterator;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.layer.LayerDeclaration;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.icon.WorldIcon;

@NotThreadSafe
public class FragmentGraph implements Iterable<FragmentGraphItem> {
	private final Iterable<LayerDeclaration> declarations;
	private final FragmentManager fragmentManager;

	private FragmentGraphItem topLeftFragment;
	private int fragmentsPerRow;
	private int fragmentsPerColumn;

	@CalledOnlyBy(AmidstThread.EDT)
	public FragmentGraph(Iterable<LayerDeclaration> declarations,
			FragmentManager fragmentManager) {
		this.declarations = declarations;
		this.fragmentManager = fragmentManager;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private FragmentGraphItem getTopLeftFragment() {
		if (topLeftFragment == null) {
			init(CoordinatesInWorld.origin());
		}
		return topLeftFragment;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void adjust(int newLeft, int newAbove, int newRight, int newBelow) {
		topLeftFragment = getTopLeftFragment().adjustRowsAndColumns(newAbove,
				newBelow, newLeft, newRight, fragmentManager);
		fragmentsPerRow = fragmentsPerRow + newLeft + newRight;
		fragmentsPerColumn = fragmentsPerColumn + newAbove + newBelow;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void init(CoordinatesInWorld coordinates) {
		recycleAll();
		topLeftFragment = new FragmentGraphItem(
				fragmentManager.requestFragment(coordinates.toFragmentCorner()));
		fragmentsPerRow = 1;
		fragmentsPerColumn = 1;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void dispose() {
		recycleAll();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void recycleAll() {
		if (topLeftFragment != null) {
			topLeftFragment.recycleAll(fragmentManager);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public int getFragmentsPerRow() {
		return fragmentsPerRow;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public int getFragmentsPerColumn() {
		return fragmentsPerColumn;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public CoordinatesInWorld getCorner() {
		return topLeftFragment.getFragment().getCorner();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	public Iterator<FragmentGraphItem> iterator() {
		return getTopLeftFragment().iterator();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public WorldIcon getClosestWorldIcon(CoordinatesInWorld coordinates,
			double maxDistanceInWorld) {
		return new ClosestWorldIconFinder(this, declarations, coordinates,
				maxDistanceInWorld).getWorldIcon();
	}

	@CalledOnlyBy(AmidstThread.EDT)
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
