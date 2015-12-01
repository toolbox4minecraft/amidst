package amidst.map;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.List;

import amidst.Options;
import amidst.map.layer.Layer;
import amidst.map.layer.LayerType;
import amidst.minecraft.world.CoordinatesInWorld;
import amidst.minecraft.world.World;
import amidst.minecraft.world.object.WorldObject;

public class Map {
	private WorldObject selectedWorldObject;

	private Fragment startFragment;
	private Point2D.Double startOnScreen = new Point2D.Double();

	private int fragmentsPerRow;
	private int fragmentsPerColumn;
	private int viewerWidth = 1;
	private int viewerHeight = 1;

	private final Object mapLock = new Object();

	private final MapZoom zoom;
	private final BiomeSelection biomeSelection;
	private final FragmentManager fragmentManager;
	private final LayerContainer layerContainer;

	public Map(FragmentCache fragmentCache, World world, MapZoom zoom,
			BiomeSelection biomeSelection) {
		this.zoom = zoom;
		this.biomeSelection = biomeSelection;
		this.fragmentManager = fragmentCache.createFragmentManager(world, this);
		this.layerContainer = fragmentManager.getLayerContainer();
	}

	private void lockedDraw(MapDrawer drawer) {
		double fragmentSizeOnScreen = zoom.worldToScreen(Fragment.SIZE);
		int desiredFragmentsPerRow = (int) (viewerWidth / fragmentSizeOnScreen + 2);
		int desiredFragmentsPerColumn = (int) (viewerHeight
				/ fragmentSizeOnScreen + 2);
		lockedAdjustNumberOfRowsAndColumns(fragmentSizeOnScreen,
				desiredFragmentsPerRow, desiredFragmentsPerColumn);
		if (startFragment != null) {
			drawer.doDrawMap(startOnScreen, startFragment);
		}
	}

	private void lockedAdjustNumberOfRowsAndColumns(
			double fragmentSizeOnScreen, int desiredFragmentsPerRow,
			int desiredFragmentsPerColumn) {
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

	public void moveBy(int deltaX, int deltaY) {
		startOnScreen.x += deltaX;
		startOnScreen.y += deltaY;
	}

	public void moveBy(Point2D.Double delta) {
		startOnScreen.x += delta.x;
		startOnScreen.y += delta.y;
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

	public WorldObject getWorldObjectAt(Point positionOnScreen,
			double maxDistance) {
		double xCornerOnScreen = startOnScreen.x;
		double yCornerOnScreen = startOnScreen.y;
		WorldObject closestWorldObject = null;
		double closestDistance = maxDistance;
		double fragmentSizeOnScreen = zoom.worldToScreen(Fragment.SIZE);
		if (startFragment != null) {
			for (Fragment fragment : startFragment) {
				for (Layer layer : layerContainer.getAllLayers()) {
					if (layer.getLayerDeclaration().isVisible()) {
						for (WorldObject worldObject : fragment
								.getWorldObjects(layer.getLayerDeclaration()
										.getLayerType())) {
							double distance = getDistance(positionOnScreen,
									xCornerOnScreen, yCornerOnScreen,
									worldObject);
							if (closestDistance > distance) {
								closestDistance = distance;
								closestWorldObject = worldObject;
							}
						}
					}
				}
				xCornerOnScreen += fragmentSizeOnScreen;
				if (fragment.isEndOfLine()) {
					xCornerOnScreen = startOnScreen.x;
					yCornerOnScreen += fragmentSizeOnScreen;
				}
			}
		}
		return closestWorldObject;
	}

	private double getDistance(Point positionOnScreen, double xCornerOnScreen,
			double yCornerOnScreen, WorldObject worldObject) {
		return worldToScreen(worldObject.getCoordinates(), xCornerOnScreen,
				yCornerOnScreen).distance(positionOnScreen);
	}

	private Point2D.Double worldToScreen(CoordinatesInWorld coordinates,
			double xCornerOnScreen, double yCornerOnScreen) {
		double x = zoom.worldToScreen(coordinates.getXRelativeToFragment());
		double y = zoom.worldToScreen(coordinates.getYRelativeToFragment());
		return new Point2D.Double(xCornerOnScreen + x, yCornerOnScreen + y);
	}

	public CoordinatesInWorld screenToWorld(Point pointOnScreen) {
		// TODO: what to do if startFragment == null? ... should never happen
		return startFragment.getCorner().add(
				(long) zoom.screenToWorld(pointOnScreen.x - startOnScreen.x),
				(long) zoom.screenToWorld(pointOnScreen.y - startOnScreen.y));
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

	public WorldObject getSelectedWorldObject() {
		return selectedWorldObject;
	}

	public void selectWorldObjectAt(Point mouse, double maxDistance) {
		this.selectedWorldObject = getWorldObjectAt(mouse, maxDistance);
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

	public List<Layer> getAllLayers() {
		return layerContainer.getAllLayers();
	}

	public BiomeSelection getBiomeSelection() {
		return biomeSelection;
	}

	public void tickFragmentLoader() {
		fragmentManager.tick();
	}
}
