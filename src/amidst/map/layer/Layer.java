package amidst.map.layer;

import amidst.map.Map;
import amidst.minecraft.world.World;

public abstract class Layer {
	private final LayerType layerType;
	private World world;
	private Map map;

	public Layer(LayerType layerType) {
		this.layerType = layerType;
	}

	public LayerType getLayerType() {
		return layerType;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	protected World getWorld() {
		return world;
	}

	public void setMap(Map map) {
		this.map = map;
	}

	protected Map getMap() {
		return map;
	}

	public boolean isVisible() {
		return true;
	}
}
