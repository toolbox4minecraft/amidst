package amidst.map;

import java.util.List;

import amidst.fragment.layer.LayerDeclaration;
import amidst.minecraft.world.CoordinatesInWorld;
import amidst.minecraft.world.icon.WorldIcon;

public class ClosestWorldIconFinder {
	private final List<LayerDeclaration> layerDeclarations;
	private final CoordinatesInWorld positionInWorld;
	private final Fragment startFragment;
	private WorldIcon closestIcon;
	private double closestDistanceSq;

	public ClosestWorldIconFinder(List<LayerDeclaration> layerDeclarations,
			CoordinatesInWorld positionInWorld, Fragment startFragment,
			double maxDistanceInWorld) {
		this.layerDeclarations = layerDeclarations;
		this.positionInWorld = positionInWorld;
		this.startFragment = startFragment;
		this.closestIcon = null;
		this.closestDistanceSq = maxDistanceInWorld * maxDistanceInWorld;
		find();
	}

	private void find() {
		for (Fragment fragment : startFragment) {
			for (LayerDeclaration declaration : layerDeclarations) {
				if (declaration.isVisible()) {
					int layerId = declaration.getLayerId();
					for (WorldIcon icon : fragment.getWorldIcons(layerId)) {
						updateClosest(icon);
					}
				}
			}
		}
	}

	private void updateClosest(WorldIcon icon) {
		double distanceSq = icon.getCoordinates()
				.getDistanceSq(positionInWorld);
		if (closestDistanceSq > distanceSq) {
			closestDistanceSq = distanceSq;
			closestIcon = icon;
		}
	}

	public boolean hasResult() {
		return closestIcon != null;
	}

	public WorldIcon getWorldIcon() {
		return closestIcon;
	}

	public double getDistance() {
		return Math.sqrt(closestDistanceSq);
	}
}
