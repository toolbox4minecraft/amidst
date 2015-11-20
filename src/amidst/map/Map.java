package amidst.map;

import java.awt.Point;
import java.awt.geom.Point2D;

import amidst.Options;
import amidst.map.layer.ImageLayer;
import amidst.map.layer.LiveLayer;
import amidst.map.object.MapObject;
import amidst.minecraft.Biome;
import amidst.minecraft.world.FileWorld.Player;
import amidst.utilities.CoordinateUtils;

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
		startFragment = fragmentManager.requestFragment(x, y);
		fragmentsPerRow = 1;
		fragmentsPerColumn = 1;
	}

	// TODO: Support longs?
	private void lockedCenterOn(long xInWorld, long yInWorld) {
		if (startFragment != null) {
			startFragment = startFragment.recycleAll(fragmentManager);
		}
		int xCenterOnScreen = viewerWidth >> 1;
		int yCenterOnScreen = viewerHeight >> 1;
		long xFragmentRelative = CoordinateUtils.toFragmentRelative(xInWorld);
		long yFragmentRelative = CoordinateUtils.toFragmentRelative(yInWorld);
		startOnScreen.x = xCenterOnScreen
				- zoom.worldToScreen(xFragmentRelative);
		startOnScreen.y = yCenterOnScreen
				- zoom.worldToScreen(yFragmentRelative);
		long xFragmentCorner = CoordinateUtils.toFragmentCorner(xInWorld);
		long yFragmentCorner = CoordinateUtils.toFragmentCorner(yInWorld);
		lockedAddStart((int) xFragmentCorner, (int) yFragmentCorner);
	}

	public void safeDraw(MapDrawer drawer) {
		synchronized (mapLock) {
			lockedDraw(drawer);
		}
	}

	public void safeCenterOn(long x, long y) {
		synchronized (mapLock) {
			lockedCenterOn(x, y);
		}
	}

	public void safeDispose() {
		synchronized (mapLock) {
			lockedDispose();
		}
	}

	public Fragment getFragmentAt(Point position) {
		Point cornerPosition = new Point(position.x >> Fragment.SIZE_SHIFT,
				position.y >> Fragment.SIZE_SHIFT);
		Point fragmentPosition = new Point();
		if (startFragment != null) {
			for (Fragment fragment : startFragment) {
				fragmentPosition.x = fragment.getFragmentXInWorld();
				fragmentPosition.y = fragment.getFragmentYInWorld();
				if (cornerPosition.equals(fragmentPosition)) {
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

	private Point getPositionOnScreen(double x, double y, MapObject mapObject) {
		Point result = new Point(mapObject.getXInFragment(),
				mapObject.getYInFragment());
		result.x = zoom.worldToScreen(result.x);
		result.y = zoom.worldToScreen(result.y);
		result.x += x;
		result.y += y;
		return result;
	}

	public String getBiomeNameAt(Point point) {
		if (startFragment != null) {
			for (Fragment fragment : startFragment) {
				if (fragment.isLoaded()) {
					if ((fragment.getXInWorld() <= point.x)
							&& (fragment.getYInWorld() <= point.y)
							&& (fragment.getXInWorld() + Fragment.SIZE > point.x)
							&& (fragment.getYInWorld() + Fragment.SIZE > point.y)) {
						int x = point.x - fragment.getXInWorld();
						int y = point.y - fragment.getYInWorld();

						return getBiomeNameForFragment(fragment, x, y);
					}
				}
			}
		}
		return "Unknown";
	}

	public String getBiomeAliasAt(Point point) {
		if (startFragment != null) {
			for (Fragment fragment : startFragment) {
				if (fragment.isLoaded()) {
					if ((fragment.getXInWorld() <= point.x)
							&& (fragment.getYInWorld() <= point.y)
							&& (fragment.getXInWorld() + Fragment.SIZE > point.x)
							&& (fragment.getYInWorld() + Fragment.SIZE > point.y)) {
						int x = point.x - fragment.getXInWorld();
						int y = point.y - fragment.getYInWorld();

						return getBiomeAliasForFragment(fragment, x, y);
					}
				}
			}
		}
		return "Unknown";
	}

	private String getBiomeNameForFragment(Fragment fragment, int blockX,
			int blockY) {
		return Biome.biomes[getBiomeForFragment(fragment, blockX, blockY)].name;
	}

	private String getBiomeAliasForFragment(Fragment fragment, int blockX,
			int blockY) {
		return Options.instance.biomeColorProfile
				.getAliasForId(getBiomeForFragment(fragment, blockX, blockY));
	}

	private int getBiomeForFragment(Fragment fragment, int blockX, int blockY) {
		int index = (blockY >> 2) * Fragment.BIOME_SIZE + (blockX >> 2);
		return fragment.getBiomeData()[index];
	}

	public void moveBy(Point2D.Double speed) {
		moveBy(speed.x, speed.y);
	}

	public void moveBy(double x, double y) {
		startOnScreen.x += x;
		startOnScreen.y += y;
	}

	public Point screenToWorld(Point pointOnScreen) {
		Point result = pointOnScreen.getLocation();

		result.x -= startOnScreen.x;
		result.y -= startOnScreen.y;

		result.x = zoom.screenToWorld(result.x);
		result.y = zoom.screenToWorld(result.y);

		// TODO: what to do if startFragment == null? ... should never happen
		result.x += startFragment.getXInWorld();
		result.y += startFragment.getYInWorld();

		return result;
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

	@Deprecated
	public void repaintBiomeLayer() {
		repaintImageLayer(layerContainer.getBiomeLayer());
	}

	@Deprecated
	public void updatePlayerPosition(Player player, Point newLocationOnScreen) {
		Point locationInWorld = screenToWorld(newLocationOnScreen);
		Fragment newFragment = getFragmentAt(locationInWorld);
		player.moveTo(locationInWorld.x, locationInWorld.y);
		layerContainer.getPlayerLayer().updatePlayerPosition(player,
				newFragment);
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
