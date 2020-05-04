package amidst.gui.main.viewer;

import java.awt.Point;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.Fragment;
import amidst.fragment.FragmentGraph;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;

@NotThreadSafe
public class FragmentGraphToScreenTranslator {
	private final FragmentGraph graph;
	private final Zoom zoom;

	private double leftOnScreen;
	private double topOnScreen;

	private int viewerWidth;
	private int viewerHeight;

	private boolean isFirstUpdate = true;

	@CalledOnlyBy(AmidstThread.EDT)
	public FragmentGraphToScreenTranslator(FragmentGraph graph, Zoom zoom) {
		this.graph = graph;
		this.zoom = zoom;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void update(int viewerWidth, int viewerHeight) {
		this.viewerWidth = viewerWidth;
		this.viewerHeight = viewerHeight;
		centerOnOriginIfNecessary();
		adjustNumberOfRowsAndColumns();
	}

	private void centerOnOriginIfNecessary() {
		if (isFirstUpdate) {
			isFirstUpdate = false;
			centerOn(CoordinatesInWorld.origin());
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void adjustNumberOfRowsAndColumns() {
		double fragmentSizeOnScreen = zoom.worldToScreen(Fragment.SIZE);
		int desiredFragmentsPerRow = (int) (viewerWidth / fragmentSizeOnScreen + 2);
		int desiredFragmentsPerColumn = (int) (viewerHeight / fragmentSizeOnScreen + 2);
		int newColumns = desiredFragmentsPerRow - graph.getFragmentsPerRow();
		int newRows = desiredFragmentsPerColumn - graph.getFragmentsPerColumn();
		int newLeft = getNewLeft(fragmentSizeOnScreen);
		int newAbove = getNewAbove(fragmentSizeOnScreen);
		int newRight = newColumns - newLeft;
		int newBelow = newRows - newAbove;
		graph.adjust(newLeft, newAbove, newRight, newBelow);
		adjustTopLeftOnScreen(fragmentSizeOnScreen * -newLeft, fragmentSizeOnScreen * -newAbove);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private int getNewLeft(double fragmentSizeOnScreen) {
		if (leftOnScreen > 0) {
			return (int) (leftOnScreen / fragmentSizeOnScreen) + 1;
		} else {
			return (int) (leftOnScreen / fragmentSizeOnScreen);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private int getNewAbove(double fragmentSizeOnScreen) {
		if (topOnScreen > 0) {
			return (int) (topOnScreen / fragmentSizeOnScreen) + 1;
		} else {
			return (int) (topOnScreen / fragmentSizeOnScreen);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void centerOn(final CoordinatesInWorld coordinates) {
		graph.init(coordinates);
		int xCenterOnScreen = viewerWidth >> 1;
		int yCenterOnScreen = viewerHeight >> 1;
		long xFragmentRelative = coordinates.getXRelativeToFragment();
		long yFragmentRelative = coordinates.getYRelativeToFragment();
		setTopLeftOnScreen(
				xCenterOnScreen - zoom.worldToScreen(xFragmentRelative),
				yCenterOnScreen - zoom.worldToScreen(yFragmentRelative));
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void adjustToMovement(int deltaX, int deltaY) {
		adjustTopLeftOnScreen(deltaX, deltaY);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void adjustToZoom(double previous, double current, Point mousePosition) {
		double baseX = mousePosition.x - leftOnScreen;
		double baseY = mousePosition.y - topOnScreen;
		double deltaX = baseX - (baseX / previous) * current;
		double deltaY = baseY - (baseY / previous) * current;
		adjustTopLeftOnScreen(deltaX, deltaY);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void setTopLeftOnScreen(double leftOnScreen, double topOnScreen) {
		this.leftOnScreen = leftOnScreen;
		this.topOnScreen = topOnScreen;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void adjustTopLeftOnScreen(double deltaX, double deltaY) {
		this.leftOnScreen += deltaX;
		this.topOnScreen += deltaY;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public double getLeftOnScreen() {
		return leftOnScreen;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public double getTopOnScreen() {
		return topOnScreen;
	}
	
	public double getWidth() {
		return viewerWidth;
	}
	
	public double getHeight() {
		return viewerHeight;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public CoordinatesInWorld screenToWorld(Point pointOnScreen) {
		CoordinatesInWorld corner = graph.getCorner();
		return corner.add(
				(long) zoom.screenToWorld(pointOnScreen.x - leftOnScreen),
				(long) zoom.screenToWorld(pointOnScreen.y - topOnScreen));
	}
}
