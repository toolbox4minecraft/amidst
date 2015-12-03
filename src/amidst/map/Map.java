package amidst.map;

import java.awt.Point;
import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.minecraft.world.CoordinatesInWorld;
import amidst.minecraft.world.icon.WorldIcon;

public class Map {
	private static final String UNKNOWN_BIOME_ALIAS = "Unknown";

	private final ConcurrentLinkedQueue<Runnable> tasks = new ConcurrentLinkedQueue<Runnable>();

	private final Zoom zoom;
	private final FragmentGraph graph;

	private volatile double startXOnScreen;
	private volatile double startYOnScreen;

	private volatile int viewerWidth = 1;
	private volatile int viewerHeight = 1;

	public Map(Zoom zoom, FragmentGraph graph) {
		this.zoom = zoom;
		this.graph = graph;
		invokeLater(new Runnable() {
			@Override
			public void run() {
				startXOnScreen = viewerWidth >> 1;
				startYOnScreen = viewerHeight >> 1;
			}
		});
	}

	public void setViewerDimensions(int viewerWidth, int viewerHeight) {
		this.viewerWidth = viewerWidth;
		this.viewerHeight = viewerHeight;
	}

	public void processTasks() {
		Runnable task;
		while ((task = tasks.poll()) != null) {
			task.run();
		}
	}

	public void adjustNumberOfRowsAndColumns() {
		double fragmentSizeOnScreen = zoom.worldToScreen(Fragment.SIZE);
		int desiredFragmentsPerRow = (int) (viewerWidth / fragmentSizeOnScreen + 2);
		int desiredFragmentsPerColumn = (int) (viewerHeight
				/ fragmentSizeOnScreen + 2);
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

	public void centerOn(final CoordinatesInWorld coordinates) {
		invokeLater(new Runnable() {
			@Override
			public void run() {
				graph.init(coordinates);
				doCenterOn(coordinates);
			}
		});
	}

	// TODO: Support longs?
	private void doCenterOn(CoordinatesInWorld coordinates) {
		int xCenterOnScreen = viewerWidth >> 1;
		int yCenterOnScreen = viewerHeight >> 1;
		long xFragmentRelative = coordinates.getXRelativeToFragment();
		long yFragmentRelative = coordinates.getYRelativeToFragment();
		startXOnScreen = xCenterOnScreen
				- zoom.worldToScreen(xFragmentRelative);
		startYOnScreen = yCenterOnScreen
				- zoom.worldToScreen(yFragmentRelative);
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
		for (FragmentGraphItem fragmentGraphItem : graph) {
			Fragment fragment = fragmentGraphItem.getFragment();
			if (corner.equals(fragment.getCorner())) {
				return fragment;
			}
		}
		return null;
	}

	public WorldIcon getClosestWorldIcon(Point mousePosition, double maxDistance) {
		return graph.getClosestWorldIcon(screenToWorld(mousePosition),
				zoom.screenToWorld(maxDistance));
	}

	public CoordinatesInWorld screenToWorld(Point pointOnScreen) {
		CoordinatesInWorld corner = graph.getCorner();
		return corner.add(
				(long) zoom.screenToWorld(pointOnScreen.x - startXOnScreen),
				(long) zoom.screenToWorld(pointOnScreen.y - startYOnScreen));
	}

	private void invokeLater(Runnable task) {
		tasks.offer(task);
	}

	public double getStartXOnScreen() {
		return startXOnScreen;
	}

	public double getStartYOnScreen() {
		return startYOnScreen;
	}
}
