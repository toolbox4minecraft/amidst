package amidst.map;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.List;

import amidst.fragment.layer.LayerDeclaration;
import amidst.minecraft.world.CoordinatesInWorld;
import amidst.minecraft.world.icon.WorldIcon;

public class ClosestWorldIconFinder {
	private final Fragment startFragment;
	private final double startXOnScreen;
	private final MapZoom zoom;
	private final List<LayerDeclaration> layerDeclarations;
	private double xCornerOnScreen;
	private double yCornerOnScreen;
	private final double fragmentSizeOnScreen;
	private WorldIcon closestIcon;
	private double closestDistanceSq;
	private Point positionOnScreen;

	public ClosestWorldIconFinder(Fragment startFragment,
			double startXOnScreen, double startYOnScreen, MapZoom zoom,
			List<LayerDeclaration> layerDeclarations, double maxDistance,
			Point positionOnScreen) {
		this.startFragment = startFragment;
		this.startXOnScreen = startXOnScreen;
		this.xCornerOnScreen = startXOnScreen;
		this.yCornerOnScreen = startYOnScreen;
		this.zoom = zoom;
		this.layerDeclarations = layerDeclarations;
		this.positionOnScreen = positionOnScreen;
		this.fragmentSizeOnScreen = zoom.worldToScreen(Fragment.SIZE);
		this.closestIcon = null;
		this.closestDistanceSq = maxDistance * maxDistance;
		findClosest();
	}

	private void findClosest() {
		for (Fragment fragment : startFragment) {
			for (LayerDeclaration declaration : layerDeclarations) {
				if (declaration.isVisible()) {
					for (WorldIcon icon : fragment.getWorldIcons(declaration
							.getLayerId())) {
						updateClosestDistance(icon);
					}
				}
			}
			fragmentFinished(fragment);
		}
	}

	private void updateClosestDistance(WorldIcon icon) {
		double distanceSq = getDistanceSq(icon);
		if (closestDistanceSq > distanceSq) {
			closestDistanceSq = distanceSq;
			closestIcon = icon;
		}
	}

	private void fragmentFinished(Fragment fragment) {
		xCornerOnScreen += fragmentSizeOnScreen;
		if (fragment.isEndOfLine()) {
			xCornerOnScreen = startXOnScreen;
			yCornerOnScreen += fragmentSizeOnScreen;
		}
	}

	private double getDistanceSq(WorldIcon icon) {
		CoordinatesInWorld coordinates = icon.getCoordinates();
		double x = zoom.worldToScreen(coordinates.getXRelativeToFragment());
		double y = zoom.worldToScreen(coordinates.getYRelativeToFragment());
		return Point2D.distanceSq(xCornerOnScreen + x, yCornerOnScreen + y,
				positionOnScreen.x, positionOnScreen.y);
	}

	public WorldIcon getWorldIcon() {
		return closestIcon;
	}

	public double getDistance() {
		return Math.sqrt(closestDistanceSq);
	}
}
