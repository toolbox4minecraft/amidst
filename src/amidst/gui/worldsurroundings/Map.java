package amidst.gui.worldsurroundings;

import java.awt.Point;

import amidst.fragment.Fragment;
import amidst.fragment.FragmentGraph;
import amidst.minecraft.world.CoordinatesInWorld;
import amidst.minecraft.world.icon.WorldIcon;
import amidst.utilities.TaskQueue;

public class Map {
	private final TaskQueue taskQueue = new TaskQueue();

	private final Zoom zoom;
	private final FragmentGraph graph;

	private volatile double startXOnScreen;
	private volatile double startYOnScreen;

	private volatile int viewerWidth = 1;
	private volatile int viewerHeight = 1;

	public Map(Zoom zoom, FragmentGraph graph) {
		this.zoom = zoom;
		this.graph = graph;
		centerOn(CoordinatesInWorld.origin());
	}

	public void setViewerDimensions(int viewerWidth, int viewerHeight) {
		this.viewerWidth = viewerWidth;
		this.viewerHeight = viewerHeight;
	}

	public void processTasks() {
		taskQueue.processTasks();
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
		taskQueue.invoke(new Runnable() {
			@Override
			public void run() {
				doCenterOn(coordinates);
			}
		});
	}

	// TODO: Support longs?
	private void doCenterOn(CoordinatesInWorld coordinates) {
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

	public double getStartXOnScreen() {
		return startXOnScreen;
	}

	public double getStartYOnScreen() {
		return startYOnScreen;
	}
}
