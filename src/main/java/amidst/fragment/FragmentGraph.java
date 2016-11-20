package amidst.fragment;

import java.util.Iterator;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.layer.LayerDeclaration;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.icon.WorldIcon;
import amidst.util.Lazy;

@NotThreadSafe
public class FragmentGraph implements Iterable<FragmentGraphItem> {
	private final Iterable<LayerDeclaration> declarations;
	private final FragmentManager fragmentManager;

	private int fragmentsPerRow;
	private int fragmentsPerColumn;
	private final Lazy<FragmentGraphItem> topLeftFragment = Lazy.from(this::createOrigin);

	@CalledOnlyBy(AmidstThread.EDT)
	public FragmentGraph(Iterable<LayerDeclaration> declarations, FragmentManager fragmentManager) {
		this.declarations = declarations;
		this.fragmentManager = fragmentManager;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void init(CoordinatesInWorld coordinates) {
		topLeftFragment.setToValue(create(coordinates));
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private FragmentGraphItem createOrigin() {
		return create(CoordinatesInWorld.origin());
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private FragmentGraphItem create(CoordinatesInWorld coordinates) {
		recycleAll();
		fragmentsPerRow = 1;
		fragmentsPerColumn = 1;
		return new FragmentGraphItem(fragmentManager.requestFragment(coordinates.toFragmentCorner()));
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void adjust(int newLeft, int newAbove, int newRight, int newBelow) {
		fragmentsPerRow = fragmentsPerRow + newLeft + newRight;
		fragmentsPerColumn = fragmentsPerColumn + newAbove + newBelow;
		topLeftFragment
				.replaceWithValue(f -> f.adjustRowsAndColumns(newAbove, newBelow, newLeft, newRight, fragmentManager));
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void dispose() {
		recycleAll();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void recycleAll() {
		topLeftFragment.ifInitialized(f -> f.recycleAll(fragmentManager));
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
		return topLeftFragment.getOrCreateValue().getFragment().getCorner();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	public Iterator<FragmentGraphItem> iterator() {
		return topLeftFragment.getOrCreateValue().iterator();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public WorldIcon getClosestWorldIcon(CoordinatesInWorld coordinates, double maxDistanceInWorld) {
		return new ClosestWorldIconFinder(this, declarations, coordinates, maxDistanceInWorld).getWorldIcon();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public Fragment getFragmentAt(CoordinatesInWorld coordinates) {
		CoordinatesInWorld corner = coordinates.toFragmentCorner();
		for (FragmentGraphItem fragmentGraphItem : topLeftFragment.getOrCreateValue()) {
			Fragment fragment = fragmentGraphItem.getFragment();
			if (corner.equals(fragment.getCorner())) {
				return fragment;
			}
		}
		return null;
	}
}
