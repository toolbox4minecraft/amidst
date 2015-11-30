package amidst.map.layer;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import amidst.map.Fragment;
import amidst.map.Map;
import amidst.minecraft.world.World;

public abstract class Layer {
	protected final World world;
	protected final Map map;
	protected final LayerType layerType;

	public Layer(World world, Map map, LayerType layerType) {
		this.world = world;
		this.map = map;
		this.layerType = layerType;
	}

	public LayerType getLayerType() {
		return layerType;
	}

	public boolean isVisible() {
		return true;
	}

	public void construct(Fragment fragment) {
	}

	public abstract void load(Fragment fragment);

	public abstract void reload(Fragment fragment);

	public abstract void draw(Fragment fragment, Graphics2D g2d,
			AffineTransform layerMatrix);
}
