package amidst.map.layer;

import amidst.map.Map;
import amidst.minecraft.world.World;

public abstract class Layer {
	private World world;
	private Map map;

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
