package amidst.map.layer;

import amidst.map.Fragment;
import amidst.map.object.MapObject;
import amidst.minecraft.world.finder.WorldObject;
import amidst.minecraft.world.finder.WorldObjectConsumer;

public abstract class IconLayer extends Layer {
	public abstract void generateMapObjects(Fragment fragment);

	protected WorldObjectConsumer createWorldObjectConsumer(
			final Fragment fragment) {
		return new WorldObjectConsumer() {
			@Override
			public void consume(WorldObject worldObject) {
				fragment.addObject(new MapObject(worldObject, IconLayer.this));
			}
		};
	}
}
