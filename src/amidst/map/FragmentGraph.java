package amidst.map;

import java.util.Iterator;
import java.util.List;

import amidst.fragment.layer.LayerDeclaration;
import amidst.minecraft.world.CoordinatesInWorld;
import amidst.minecraft.world.icon.WorldIcon;

public class FragmentGraph implements Iterable<Fragment> {
	private final List<LayerDeclaration> declarations;
	private final FragmentManager fragmentManager;

	private volatile FragmentGraphItem startFragment;
	private volatile int fragmentsPerRow;
	private volatile int fragmentsPerColumn;

	public FragmentGraph(List<LayerDeclaration> declarations,
			FragmentManager fragmentManager) {
		this.declarations = declarations;
		this.fragmentManager = fragmentManager;
	}

	private FragmentGraphItem getStartFragment() {
		if (startFragment == null) {
			init(CoordinatesInWorld.origin());
		}
		return startFragment;
	}

	public void adjust(int newLeft, int newAbove, int newRight, int newBelow) {
		startFragment = getStartFragment().adjustRowsAndColumns(newAbove,
				newBelow, newLeft, newRight, fragmentManager);
		fragmentsPerRow = fragmentsPerRow + newLeft + newRight;
		fragmentsPerColumn = fragmentsPerColumn + newAbove + newBelow;
	}

	public void init(CoordinatesInWorld coordinates) {
		recycleAll();
		startFragment = fragmentManager.requestFragment(coordinates
				.toFragmentCorner());
		fragmentsPerRow = 1;
		fragmentsPerColumn = 1;
	}

	public void dispose() {
		recycleAll();
	}

	private void recycleAll() {
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

	public CoordinatesInWorld getCorner() {
		return startFragment.getCorner();
	}

	@Override
	public Iterator<Fragment> iterator() {
		return getStartFragment().iterator();
	}

	public WorldIcon getClosestWorldIcon(CoordinatesInWorld coordinates,
			double maxDistanceInWorld) {
		return new ClosestWorldIconFinder(this, declarations, coordinates,
				maxDistanceInWorld).getWorldIcon();
	}
}
