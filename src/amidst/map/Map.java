package amidst.map;

import java.awt.Point;
import java.awt.geom.Point2D;

import amidst.Options;
import amidst.map.layer.IconLayer;
import amidst.map.layer.ImageLayer;
import amidst.map.layer.LiveLayer;
import amidst.map.object.MapObject;
import amidst.minecraft.world.CoordinatesInWorld;

public class Map {
	private MapObject selectedMapObject;

	private Fragment startFragment;

	private Point2D.Double startOnScreen = new Point2D.Double();

	private int fragmentsPerRow;
	private int fragmentsPerColumn;
	private int viewerWidth = 1;
	private int viewerHeight = 1;

	private final Object mapLock = new Object();

	private FragmentManager fragmentManager;
	private MapZoom zoom;
	private LayerContainer layerContainer;

	public Map(FragmentManager fragmentManager, MapZoom zoom,
			LayerContainer layerContainer) {
		this.fragmentManager = fragmentManager;
		this.zoom = zoom;
		this.layerContainer = layerContainer;
		this.layerContainer.reloadAllLayers(this);
	}

	private void lockedDraw(MapDrawer drawer) {
		int fragmentSizeOnScreen = zoom.worldToScreen(Fragment.SIZE);
		int desiredFragmentsPerRow = viewerWidth / fragmentSizeOnScreen + 2;
		int desiredFragmentsPerColumn = viewerHeight / fragmentSizeOnScreen + 2;
		lockedAdjustNumberOfRowsAndColumns(fragmentSizeOnScreen,
				desiredFragmentsPerRow, desiredFragmentsPerColumn);
		if (startFragment != null) {
			drawer.doDrawMap(startOnScreen, startFragment);
		}
	}

	private void lockedAdjustNumberOfRowsAndColumns(int fragmentSizeOnScreen,
			int desiredFragmentsPerRow, int desiredFragmentsPerColumn) {
		int newColumns = desiredFragmentsPerRow - fragmentsPerRow;
		int newRows = desiredFragmentsPerColumn - fragmentsPerColumn;
		int newLeft = 0;
		int newAbove = 0;
		while (startOnScreen.x > 0) {
			startOnScreen.x -= fragmentSizeOnScreen;
			newLeft++;
		}
		while (startOnScreen.x < -fragmentSizeOnScreen) {
			startOnScreen.x += fragmentSizeOnScreen;
			newLeft--;
		}
		while (startOnScreen.y > 0) {
			startOnScreen.y -= fragmentSizeOnScreen;
			newAbove++;
		}
		while (startOnScreen.y < -fragmentSizeOnScreen) {
			startOnScreen.y += fragmentSizeOnScreen;
			newAbove--;
		}
		int newRight = newColumns - newLeft;
		int newBelow = newRows - newAbove;
		startFragment = startFragment.adjustRowsAndColumns(newAbove, newBelow,
				newLeft, newRight, fragmentManager);
		fragmentsPerRow = fragmentsPerRow + newLeft + newRight;
		fragmentsPerColumn = fragmentsPerColumn + newAbove + newBelow;
	}

	private void lockedAddStart(int x, int y) {
		startFragment = fragmentManager.requestFragment(CoordinatesInWorld
				.origin());
		fragmentsPerRow = 1;
		fragmentsPerColumn = 1;
	}

	// TODO: Support longs?
	private void lockedCenterOn(CoordinatesInWorld coordinates) {
		if (startFragment != null) {
			startFragment = startFragment.recycleAll(fragmentManager);
		}
		int xCenterOnScreen = viewerWidth >> 1;
		int yCenterOnScreen = viewerHeight >> 1;
		long xFragmentRelative = coordinates.getXRelativeToFragment();
		long yFragmentRelative = coordinates.getYRelativeToFragment();
		startOnScreen.x = xCenterOnScreen
				- zoom.worldToScreen(xFragmentRelative);
		startOnScreen.y = yCenterOnScreen
				- zoom.worldToScreen(yFragmentRelative);
		long xFragmentCorner = coordinates.getXCornerOfFragment();
		long yFragmentCorner = coordinates.getYCornerOfFragment();
		lockedAddStart((int) xFragmentCorner, (int) yFragmentCorner);
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

	public Fragment getFragmentAt(CoordinatesInWorld coordinates) {
		CoordinatesInWorld corner = coordinates.toFragmentCorner();
		if (startFragment != null) {
			for (Fragment fragment : startFragment) {
				if (corner.equals(fragment.getCorner())) {
					return fragment;
				}
			}
		}
		return null;
	}

	public MapObject getMapObjectAt(Point positionOnScreen, double maxRange) {
		double x = startOnScreen.x;
		double y = startOnScreen.y;
		MapObject closestObject = null;
		double closestDistance = maxRange;
		int fragmentSizeOnScreen = zoom.worldToScreen(Fragment.SIZE);
		if (startFragment != null) {
			for (Fragment fragment : startFragment) {
				for (MapObject mapObject : fragment.getMapObjects()) {
					if (mapObject.isVisible()) {
						double distance = getPositionOnScreen(x, y, mapObject)
								.distance(positionOnScreen);
						if (closestDistance > distance) {
							closestDistance = distance;
							closestObject = mapObject;
						}
					}
				}
				x += fragmentSizeOnScreen;
				if (fragment.isEndOfLine()) {
					x = startOnScreen.x;
					y += fragmentSizeOnScreen;
				}
			}
		}
		return closestObject;
	}

	// TODO: use longs?
	private Point getPositionOnScreen(double x, double y, MapObject mapObject) {
		Point result = new Point((int) mapObject.getCoordinates()
				.getXRelativeToFragment(), (int) mapObject.getCoordinates()
				.getYRelativeToFragment());
		result.x = zoom.worldToScreen(result.x);
		result.y = zoom.worldToScreen(result.y);
		result.x += x;
		result.y += y;
		return result;
	}

	public String getBiomeAliasAt(CoordinatesInWorld coordinates) {
		if (startFragment != null) {
			for (Fragment fragment : startFragment) {
				if (fragment.isLoaded()) {
					if (fragment.isInBounds(coordinates)) {
						return getBiomeAliasForFragment(fragment, coordinates);
					}
				}
			}
		}
		return "Unknown";
	}

	private String getBiomeAliasForFragment(Fragment fragment,
			CoordinatesInWorld coordinates) {
		return Options.instance.biomeColorProfile.getAliasForId(fragment
				.getBiomeAt(coordinates));
	}

	public void moveBy(Point2D.Double speed) {
		moveBy(speed.x, speed.y);
	}

	public void moveBy(double x, double y) {
		startOnScreen.x += x;
		startOnScreen.y += y;
	}

	public CoordinatesInWorld screenToWorld(Point pointOnScreen) {
		Point result = pointOnScreen.getLocation();

		result.x -= startOnScreen.x;
		result.y -= startOnScreen.y;

		result.x = zoom.screenToWorld(result.x);
		result.y = zoom.screenToWorld(result.y);

		// TODO: what to do if startFragment == null? ... should never happen
		return startFragment.getCorner().add(result.x, result.y);
	}

	public Point2D.Double getDeltaOnScreenForSamePointInWorld(double oldScale,
			double newScale, Point newPointOnScreen) {
		double baseX = newPointOnScreen.x - startOnScreen.x;
		double baseY = newPointOnScreen.y - startOnScreen.y;

		double scaledX = baseX - (baseX / oldScale) * newScale;
		double scaledY = baseY - (baseY / oldScale) * newScale;

		return new Point2D.Double(scaledX, scaledY);
	}

	private void repaintImageLayer(ImageLayer imageLayer) {
		if (startFragment != null) {
			for (Fragment fragment : startFragment) {
				fragment.invalidateImageLayer(imageLayer.getLayerId());
			}
			fragmentManager.reloadAll();
		}
	}

	private void reloadIconLayer(IconLayer iconLayer) {
		if (startFragment != null) {
			for (Fragment fragment : startFragment) {
				fragment.invalidateIconLayer(iconLayer);
			}
			fragmentManager.reloadAll();
		}
	}

	private void lockedDispose() {
		fragmentManager.reset();
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

	public MapObject getSelectedMapObject() {
		return selectedMapObject;
	}

	public void setSelectedMapObject(MapObject selectedMapObject) {
		this.selectedMapObject = selectedMapObject;
	}

	public FragmentManager getFragmentManager() {
		return fragmentManager;
	}

	public void repaintBiomeLayer() {
		repaintImageLayer(layerContainer.getBiomeLayer());
	}

	public void reloadPlayerLayer() {
		reloadIconLayer(layerContainer.getPlayerLayer());
	}

	public void updateAllLayers(float time) {
		layerContainer.updateAllLayers(time);
	}

	public LiveLayer[] getLiveLayers() {
		return layerContainer.getLiveLayers();
	}

	public ImageLayer[] getImageLayers() {
		return layerContainer.getImageLayers();
	}
}
