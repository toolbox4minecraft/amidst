package amidst.map;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.List;

import amidst.Options;
import amidst.map.layer.IconLayer;
import amidst.map.layer.ImageLayer;
import amidst.map.layer.LayerType;
import amidst.map.layer.LiveLayer;
import amidst.map.layer.MapObject;
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
		this.layerContainer.setMap(this);
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
		startFragment = fragmentManager.requestFragment(coordinates
				.toFragmentCorner());
		fragmentsPerRow = 1;
		fragmentsPerColumn = 1;
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

	public String getBiomeAliasAt(CoordinatesInWorld coordinates) {
		Fragment fragment = getFragmentAt(coordinates);
		if (fragment != null && fragment.isLoaded()) {
			short biome = fragment.getBiomeDataAt(coordinates);
			return Options.instance.biomeColorProfile.getAliasForId(biome);
		} else {
			return "Unknown";
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
				for (List<MapObject> mapObjects : fragment.getMapObjects()
						.values()) {
					for (MapObject mapObject : mapObjects) {
						if (mapObject.getIconLayer().isVisible()) {
							double distance = getPositionOnScreen(x, y,
									mapObject).distance(positionOnScreen);
							if (closestDistance > distance) {
								closestDistance = distance;
								closestObject = mapObject;
							}
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
		CoordinatesInWorld coordinates = mapObject.getWorldObject()
				.getCoordinates();
		Point result = new Point((int) coordinates.getXRelativeToFragment(),
				(int) coordinates.getYRelativeToFragment());
		result.x = zoom.worldToScreen(result.x);
		result.y = zoom.worldToScreen(result.y);
		result.x += x;
		result.y += y;
		return result;
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

	private void reloadLayer(LayerType layerType) {
		layerContainer.invalidateLayer(layerType);
		fragmentManager.reloadAll();
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

	public void reloadBiomeLayer() {
		reloadLayer(LayerType.BIOME);
	}

	public void reloadPlayerLayer() {
		reloadLayer(LayerType.PLAYER);
	}

	public ImageLayer[] getImageLayers() {
		return layerContainer.getImageLayers();
	}

	public LiveLayer[] getLiveLayers() {
		return layerContainer.getLiveLayers();
	}

	public IconLayer[] getIconLayers() {
		return layerContainer.getIconLayers();
	}

	public ImageLayer getImageLayer(LayerType layerType) {
		return layerContainer.getImageLayer(layerType);
	}
}
