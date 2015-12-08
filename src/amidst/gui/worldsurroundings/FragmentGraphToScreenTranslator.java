package amidst.gui.worldsurroundings;

import java.awt.Point;

import amidst.fragment.Fragment;
import amidst.fragment.FragmentGraph;
import amidst.mojangapi.world.CoordinatesInWorld;
import amidst.threading.TaskQueue;

public class FragmentGraphToScreenTranslator {
	private final TaskQueue taskQueue = new TaskQueue();

	private final FragmentGraph graph;
	private final Zoom zoom;

	private volatile double leftOnScreen;
	private volatile double topOnScreen;

	private volatile int viewerWidth;
	private volatile int viewerHeight;

	public FragmentGraphToScreenTranslator(FragmentGraph graph, Zoom zoom) {
		this.graph = graph;
		this.zoom = zoom;
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
		adjustTopLeftOnScreen(fragmentSizeOnScreen * -newLeft,
				fragmentSizeOnScreen * -newAbove);
	}

	private int getNewLeft(double fragmentSizeOnScreen) {
		if (leftOnScreen > 0) {
			return (int) (leftOnScreen / fragmentSizeOnScreen) + 1;
		} else {
			return (int) (leftOnScreen / fragmentSizeOnScreen);
		}
	}

	private int getNewAbove(double fragmentSizeOnScreen) {
		if (topOnScreen > 0) {
			return (int) (topOnScreen / fragmentSizeOnScreen) + 1;
		} else {
			return (int) (topOnScreen / fragmentSizeOnScreen);
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

	private void doCenterOn(CoordinatesInWorld coordinates) {
		graph.init(coordinates);
		int xCenterOnScreen = viewerWidth >> 1;
		int yCenterOnScreen = viewerHeight >> 1;
		long xFragmentRelative = coordinates.getXRelativeToFragment();
		long yFragmentRelative = coordinates.getYRelativeToFragment();
		setTopLeftOnScreen(
				xCenterOnScreen - zoom.worldToScreen(xFragmentRelative),
				yCenterOnScreen - zoom.worldToScreen(yFragmentRelative));
	}

	public void adjustToMovement(int deltaX, int deltaY) {
		adjustTopLeftOnScreen(deltaX, deltaY);
	}

	public void adjustToZoom(double previous, double current,
			Point mousePosition) {
		double baseX = mousePosition.x - leftOnScreen;
		double baseY = mousePosition.y - topOnScreen;
		double deltaX = baseX - (baseX / previous) * current;
		double deltaY = baseY - (baseY / previous) * current;
		adjustTopLeftOnScreen(deltaX, deltaY);
	}

	private void setTopLeftOnScreen(double leftOnScreen, double topOnScreen) {
		this.leftOnScreen = leftOnScreen;
		this.topOnScreen = topOnScreen;
	}

	private void adjustTopLeftOnScreen(double deltaX, double deltaY) {
		this.leftOnScreen += deltaX;
		this.topOnScreen += deltaY;
	}

	public double getLeftOnScreen() {
		return leftOnScreen;
	}

	public double getTopOnScreen() {
		return topOnScreen;
	}

	public CoordinatesInWorld screenToWorld(Point pointOnScreen) {
		CoordinatesInWorld corner = graph.getCorner();
		return corner.add(
				(long) zoom.screenToWorld(pointOnScreen.x - leftOnScreen),
				(long) zoom.screenToWorld(pointOnScreen.y - topOnScreen));
	}
}
