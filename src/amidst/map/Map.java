package amidst.map;

import java.awt.Point;
import java.util.List;

import amidst.fragment.layer.LayerDeclaration;
import amidst.fragment.layer.LayerIds;
import amidst.minecraft.world.CoordinatesInWorld;
import amidst.minecraft.world.World;
import amidst.minecraft.world.icon.WorldIcon;

public class Map {
	private static final String UNKNOWN_BIOME_ALIAS = "Unknown";

	private WorldIcon selectedWorldIcon;

	private volatile double startXOnScreen;
	private volatile double startYOnScreen;

	private int viewerWidth = 1;
	private int viewerHeight = 1;

	private final Object mapLock = new Object();

	private final List<LayerDeclaration> declarations;
	private final MapZoom zoom;
	private final BiomeSelection biomeSelection;
	private final FragmentGraph graph;

	public Map(List<LayerDeclaration> declarations, MapZoom zoom,
			BiomeSelection biomeSelection, FragmentManager fragmentManager,
			World world) {
		this.declarations = declarations;
		this.zoom = zoom;
		this.biomeSelection = biomeSelection;
		this.graph = new FragmentGraph(fragmentManager, this);
	}

	private void lockedDraw(MapDrawer drawer) {
		double fragmentSizeOnScreen = zoom.worldToScreen(Fragment.SIZE);
		int desiredFragmentsPerRow = (int) (viewerWidth / fragmentSizeOnScreen + 2);
		int desiredFragmentsPerColumn = (int) (viewerHeight
				/ fragmentSizeOnScreen + 2);
		lockedAdjustNumberOfRowsAndColumns(fragmentSizeOnScreen,
				desiredFragmentsPerRow, desiredFragmentsPerColumn);
		drawer.doDrawMap(startXOnScreen, startYOnScreen, graph);
	}

	private void lockedAdjustNumberOfRowsAndColumns(
			double fragmentSizeOnScreen, int desiredFragmentsPerRow,
			int desiredFragmentsPerColumn) {
		int newColumns = desiredFragmentsPerRow - graph.getFragmentsPerRow();
		int newRows = desiredFragmentsPerColumn - graph.getFragmentsPerColumn();
		int newLeft = getNewLeft(fragmentSizeOnScreen);
		int newAbove = getNewAbove(fragmentSizeOnScreen);
		int newRight = newColumns - newLeft;
		int newBelow = newRows - newAbove;
		graph.adjust(newLeft, newAbove, newRight, newBelow);
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
		graph.init(coordinates);
		int xCenterOnScreen = viewerWidth >> 1;
		int yCenterOnScreen = viewerHeight >> 1;
		long xFragmentRelative = coordinates.getXRelativeToFragment();
		long yFragmentRelative = coordinates.getYRelativeToFragment();
		startXOnScreen = xCenterOnScreen
				- zoom.worldToScreen(xFragmentRelative);
		startYOnScreen = yCenterOnScreen
				- zoom.worldToScreen(yFragmentRelative);
	}

	public void safeDraw(MapDrawer drawer, int width, int height) {
		synchronized (mapLock) {
			this.viewerWidth = width;
			this.viewerHeight = height;
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
		for (Fragment fragment : graph) {
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
		return new ClosestWorldIconFinder(declarations,
				screenToWorld(positionOnScreen), graph,
				zoom.screenToWorld(maxDistanceOnScreen)).getWorldIcon();
	}

	public CoordinatesInWorld screenToWorld(Point pointOnScreen) {
		CoordinatesInWorld corner = graph.getCorner();
		return corner.add(
				(long) zoom.screenToWorld(pointOnScreen.x - startXOnScreen),
				(long) zoom.screenToWorld(pointOnScreen.y - startYOnScreen));
	}

	private void lockedDispose() {
		graph.recycleAll();
	}

	public double getZoom() {
		return zoom.getCurrentValue();
	}

	public WorldIcon getSelectedWorldIcon() {
		return selectedWorldIcon;
	}

	public BiomeSelection getBiomeSelection() {
		return biomeSelection;
	}

	/**
	 * This method is only used for debugging purposes.
	 */
	public FragmentGraph getGraph() {
		return graph;
	}

	public void reloadBiomeLayer() {
		graph.getFragmentManager().reloadLayer(LayerIds.BIOME);
	}

	public void reloadPlayerLayer() {
		graph.getFragmentManager().reloadLayer(LayerIds.PLAYER);
	}

	public void tickFragmentLoader() {
		graph.getFragmentManager().tick();
	}
}
