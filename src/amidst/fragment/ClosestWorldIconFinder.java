package amidst.fragment;

import java.util.List;

import amidst.documentation.NotThreadSafe;
import amidst.fragment.layer.LayerDeclaration;
import amidst.mojangapi.world.CoordinatesInWorld;
import amidst.mojangapi.world.icon.WorldIcon;

@NotThreadSafe
public class ClosestWorldIconFinder {
	private final FragmentGraph graph;
	private final List<LayerDeclaration> layerDeclarations;
	private final CoordinatesInWorld positionInWorld;
	private WorldIcon closestIcon;
	private double closestDistanceSq;

	public ClosestWorldIconFinder(FragmentGraph graph,
			List<LayerDeclaration> layerDeclarations,
			CoordinatesInWorld positionInWorld, double maxDistanceInWorld) {
		this.graph = graph;
		this.layerDeclarations = layerDeclarations;
		this.positionInWorld = positionInWorld;
		this.closestIcon = null;
		this.closestDistanceSq = maxDistanceInWorld * maxDistanceInWorld;
		find();
	}

	private void find() {
		for (FragmentGraphItem fragmentGraphItem : graph) {
			Fragment fragment = fragmentGraphItem.getFragment();
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
