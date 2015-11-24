package amidst.map.layer;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import amidst.map.Fragment;
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

	public abstract void load(Fragment fragment, int[] imageCache);

	public abstract void reload(Fragment fragment, int[] imageCache);

	public abstract void draw(Fragment fragment, Graphics2D g2d,
			AffineTransform layerMatrix);
}
