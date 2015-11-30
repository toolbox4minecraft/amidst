package amidst.map.layer;

import amidst.minecraft.world.finder.WorldObject;

public class MapObject {
	private final WorldObject worldObject;

	public MapObject(WorldObject worldObject) {
		this.worldObject = worldObject;
	}

	public WorldObject getWorldObject() {
		return worldObject;
	}
}
