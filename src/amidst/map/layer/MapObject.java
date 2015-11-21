package amidst.map.layer;

import amidst.minecraft.world.finder.WorldObject;

public class MapObject {
	private WorldObject worldObject;
	private final IconLayer iconLayer;

	public MapObject(WorldObject worldObject, IconLayer iconLayer) {
		this.worldObject = worldObject;
		this.iconLayer = iconLayer;
	}

	public WorldObject getWorldObject() {
		return worldObject;
	}

	public IconLayer getIconLayer() {
		return iconLayer;
	}
}
