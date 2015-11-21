package amidst.map.object;

import java.awt.image.BufferedImage;

import amidst.map.layer.IconLayer;
import amidst.minecraft.world.CoordinatesInWorld;
import amidst.minecraft.world.finder.WorldObject;

public class MapObject {
	private WorldObject worldObject;
	private final IconLayer iconLayer;

	public MapObject(WorldObject worldObject, IconLayer iconLayer) {
		this.worldObject = worldObject;
		this.iconLayer = iconLayer;
	}

	@Deprecated
	public CoordinatesInWorld getCoordinates() {
		return worldObject.getCoordinates();
	}

	@Deprecated
	public String getName() {
		return worldObject.getName();
	}

	@Deprecated
	public BufferedImage getImage() {
		return worldObject.getImage();
	}

	@Deprecated
	public boolean isVisible() {
		return iconLayer.isVisible();
	}

	public IconLayer getIconLayer() {
		return iconLayer;
	}

	// TODO: remove me!
	@Deprecated
	@Override
	public String toString() {
		return worldObject.getName() + " at ("
				+ worldObject.getCoordinates().getX() + ", "
				+ worldObject.getCoordinates().getY() + ")";
	}
}
