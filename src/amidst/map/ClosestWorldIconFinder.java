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
	private final double startYOnScreen;
	private final MapZoom zoom;
	private final List<LayerDeclaration> layerDeclarations;

	public ClosestWorldIconFinder(Fragment startFragment,
			double startXOnScreen, double startYOnScreen, MapZoom zoom,
			List<LayerDeclaration> layerDeclarations) {
		this.startFragment = startFragment;
		this.startXOnScreen = startXOnScreen;
		this.startYOnScreen = startYOnScreen;
		this.zoom = zoom;
		this.layerDeclarations = layerDeclarations;
	}

	public WorldIcon getWorldIconAt(Point positionOnScreen, double maxDistance) {
		double xCornerOnScreen = startXOnScreen;
		double yCornerOnScreen = startYOnScreen;
		WorldIcon closestIcon = null;
		double closestDistanceSq = maxDistance * maxDistance;
		double fragmentSizeOnScreen = zoom.worldToScreen(Fragment.SIZE);
		for (Fragment fragment : startFragment) {
			for (LayerDeclaration declaration : layerDeclarations) {
				if (declaration.isVisible()) {
					for (WorldIcon icon : fragment.getWorldIcons(declaration
							.getLayerId())) {
						double distanceSq = getDistanceSq(positionOnScreen,
								xCornerOnScreen, yCornerOnScreen, icon);
						if (closestDistanceSq > distanceSq) {
							closestDistanceSq = distanceSq;
							closestIcon = icon;
						}
					}
				}
			}
			xCornerOnScreen += fragmentSizeOnScreen;
			if (fragment.isEndOfLine()) {
				xCornerOnScreen = startXOnScreen;
				yCornerOnScreen += fragmentSizeOnScreen;
			}
		}
		return closestIcon;
	}

	private double getDistanceSq(Point positionOnScreen,
			double xCornerOnScreen, double yCornerOnScreen, WorldIcon icon) {
		CoordinatesInWorld coordinates = icon.getCoordinates();
		double x = zoom.worldToScreen(coordinates.getXRelativeToFragment());
		double y = zoom.worldToScreen(coordinates.getYRelativeToFragment());
		return Point2D.distanceSq(xCornerOnScreen + x, yCornerOnScreen + y,
				positionOnScreen.x, positionOnScreen.y);
	}
}
