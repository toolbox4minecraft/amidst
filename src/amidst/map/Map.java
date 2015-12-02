package amidst.map;

import java.awt.Point;

import amidst.fragment.drawer.FragmentDrawer;
import amidst.fragment.layer.LayerIds;
import amidst.fragment.layer.LayerManager;
import amidst.fragment.layer.LayerManagerFactory;
import amidst.minecraft.world.CoordinatesInWorld;
import amidst.minecraft.world.World;
import amidst.minecraft.world.icon.WorldIcon;

public class Map {
	private static final String UNKNOWN_BIOME_ALIAS = "Unknown";

	private WorldIcon selectedWorldIcon;

	private volatile Fragment startFragment;
	private volatile double startXOnScreen;
	private volatile double startYOnScreen;

	private int fragmentsPerRow;
	private int fragmentsPerColumn;
	private int viewerWidth = 1;
	private int viewerHeight = 1;

	private final Object mapLock = new Object();

	private final MapZoom zoom;
	private final BiomeSelection biomeSelection;
	private final FragmentManager fragmentManager;
	private final LayerManager layerManager;

	public Map(MapZoom zoom, BiomeSelection biomeSelection,
			FragmentManager fragmentManager,
			LayerManagerFactory layerManagerFactory, World world) {
		this.zoom = zoom;
		this.biomeSelection = biomeSelection;
		this.fragmentManager = fragmentManager;
		this.layerManager = layerManagerFactory.createLayerManager(world, this);
		this.fragmentManager.setLayerManager(layerManager);
	}

	private void lockedDraw(MapDrawer drawer) {
		double fragmentSizeOnScreen = zoom.worldToScreen(Fragment.SIZE);
		int desiredFragmentsPerRow = (int) (viewerWidth / fragmentSizeOnScreen + 2);
		int desiredFragmentsPerColumn = (int) (viewerHeight
				/ fragmentSizeOnScreen + 2);
		lockedAdjustNumberOfRowsAndColumns(fragmentSizeOnScreen,
				desiredFragmentsPerRow, desiredFragmentsPerColumn);
		drawer.doDrawMap(startXOnScreen, startYOnScreen, getStartFragment());
	}

	private void lockedAdjustNumberOfRowsAndColumns(
			double fragmentSizeOnScreen, int desiredFragmentsPerRow,
			int desiredFragmentsPerColumn) {
		int newColumns = desiredFragmentsPerRow - fragmentsPerRow;
		int newRows = desiredFragmentsPerColumn - fragmentsPerColumn;
		int newLeft = getNewLeft(fragmentSizeOnScreen);
		int newAbove = getNewAbove(fragmentSizeOnScreen);
		int newRight = newColumns - newLeft;
		int newBelow = newRows - newAbove;
		initStartFragment(newLeft, newAbove, newRight, newBelow);
		startXOnScreen -= fragmentSizeOnScreen * newLeft;
		startYOnScreen -= fragmentSizeOnScreen * newAbove;
	}

	private int getNewLeft(double fragmentSizeOnScreen) {
		if (startXOnScreen > 0) {
			return (int) (startXOnScreen / fragmentSizeOnScreen) + 1;
		} else {
			return (int) (startXOnScreen / fragmentSizeOnScreen);
		}
	}

	private int getNewAbove(double fragmentSizeOnScreen) {
		if (startYOnScreen > 0) {
			return (int) (startYOnScreen / fragmentSizeOnScreen) + 1;
		} else {
			return (int) (startYOnScreen / fragmentSizeOnScreen);
		}
	}

	// TODO: Support longs?
	private void lockedCenterOn(CoordinatesInWorld coordinates) {
		initStartFragment(coordinates);
		int xCenterOnScreen = viewerWidth >> 1;
		int yCenterOnScreen = viewerHeight >> 1;
		long xFragmentRelative = coordinates.getXRelativeToFragment();
		long yFragmentRelative = coordinates.getYRelativeToFragment();
		startXOnScreen = xCenterOnScreen
				- zoom.worldToScreen(xFragmentRelative);
		startYOnScreen = yCenterOnScreen
				- zoom.worldToScreen(yFragmentRelative);
	}

	public void safeDraw(MapDrawer drawer) {
		synchronized (mapLock) {
			lockedDraw(drawer);
		}
	}

	public void safeCenterOn(CoordinatesInWorld coordinates) {
		synchronized (mapLock) {
			lockedCenterOn(coordinates);
		}
	}

	public void safeDispose() {
		synchronized (mapLock) {
			lockedDispose();
		}
	}

	public void adjustStartOnScreenToMovement(int deltaX, int deltaY) {
		startXOnScreen += deltaX;
		startYOnScreen += deltaY;
	}

	public void adjustStartOnScreenToZoom(double previous, double current,
			Point mousePosition) {
		double baseX = mousePosition.x - startXOnScreen;
		double baseY = mousePosition.y - startYOnScreen;
		double deltaX = baseX - (baseX / previous) * current;
		double deltaY = baseY - (baseY / previous) * current;
		startXOnScreen += deltaX;
		startYOnScreen += deltaY;
	}

	public String getBiomeAliasAt(CoordinatesInWorld coordinates) {
		Fragment fragment = getFragmentAt(coordinates);
		if (fragment != null) {
			return fragment.getBiomeAliasAt(coordinates, UNKNOWN_BIOME_ALIAS);
		} else {
			return UNKNOWN_BIOME_ALIAS;
		}
	}

	private Fragment getFragmentAt(CoordinatesInWorld coordinates) {
		CoordinatesInWorld corner = coordinates.toFragmentCorner();
		for (Fragment fragment : getStartFragment()) {
			if (corner.equals(fragment.getCorner())) {
				return fragment;
			}
		}
		return null;
	}

	public void selectWorldIconAt(Point mouse, double maxDistance) {
		this.selectedWorldIcon = getWorldIconAt(mouse, maxDistance);
	}

	private WorldIcon getWorldIconAt(Point positionOnScreen,
			double maxDistanceOnScreen) {
		return new ClosestWorldIconFinder(layerManager.getLayerDeclarations(),
				screenToWorld(positionOnScreen), getStartFragment(),
				zoom.screenToWorld(maxDistanceOnScreen)).getWorldIcon();
	}

	public CoordinatesInWorld screenToWorld(Point pointOnScreen) {
		return getStartFragment().getCorner().add(
				(long) zoom.screenToWorld(pointOnScreen.x - startXOnScreen),
				(long) zoom.screenToWorld(pointOnScreen.y - startYOnScreen));
	}

	private Fragment getStartFragment() {
		if (startFragment == null) {
			safeCenterOn(CoordinatesInWorld.origin());
		}
		return startFragment;
	}

	private void initStartFragment(int newLeft, int newAbove, int newRight,
			int newBelow) {
		startFragment = getStartFragment().adjustRowsAndColumns(newAbove,
				newBelow, newLeft, newRight, fragmentManager);
		fragmentsPerRow = fragmentsPerRow + newLeft + newRight;
		fragmentsPerColumn = fragmentsPerColumn + newAbove + newBelow;
	}

	private void initStartFragment(CoordinatesInWorld coordinates) {
		recycleAll();
		startFragment = fragmentManager.requestFragment(coordinates
				.toFragmentCorner());
		fragmentsPerRow = 1;
		fragmentsPerColumn = 1;
	}

	private void reloadLayer(int layerId) {
		layerManager.invalidateLayer(layerId);
		fragmentManager.reloadAll();
	}

	private void lockedDispose() {
		recycleAll();
	}

	private void recycleAll() {
		if (startFragment != null) {
			startFragment.recycleAll(fragmentManager);
		}
	}

	public double getZoom() {
		return zoom.getCurrentValue();
	}

	public int getFragmentsPerRow() {
		return fragmentsPerRow;
	}

	public int getFragmentsPerColumn() {
		return fragmentsPerColumn;
	}

	public void setViewerWidth(int viewerWidth) {
		this.viewerWidth = viewerWidth;
	}

	public void setViewerHeight(int viewerHeight) {
		this.viewerHeight = viewerHeight;
	}

	public WorldIcon getSelectedWorldIcon() {
		return selectedWorldIcon;
	}

	public FragmentManager getFragmentManager() {
		return fragmentManager;
	}

	public void reloadBiomeLayer() {
		reloadLayer(LayerIds.BIOME);
	}

	public void reloadPlayerLayer() {
		reloadLayer(LayerIds.PLAYER);
	}

	public Iterable<FragmentDrawer> getFragmentDrawers() {
		return layerManager.getFragmentDrawers();
	}

	public BiomeSelection getBiomeSelection() {
		return biomeSelection;
	}

	public void tickFragmentLoader() {
		fragmentManager.tick();
	}
}
