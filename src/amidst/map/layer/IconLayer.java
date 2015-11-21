package amidst.map.layer;

import amidst.map.Fragment;
import amidst.map.object.MapObject;
import amidst.minecraft.world.finder.WorldObject;
import amidst.minecraft.world.finder.WorldObjectConsumer;
import amidst.preferences.BooleanPrefModel;

public abstract class IconLayer extends Layer {
	public abstract void generateMapObjects(Fragment fragment);

	protected WorldObjectConsumer createWorldObjectConsumer(
			final Fragment fragment, final BooleanPrefModel isVisiblePreference) {
		return new WorldObjectConsumer() {
			@Override
			public void consume(WorldObject worldObject) {
				fragment.addObject(MapObject.from(worldObject,
						isVisiblePreference));
			}
		};
	}
}
