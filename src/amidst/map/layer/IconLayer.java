package amidst.map.layer;

import amidst.map.Fragment;
import amidst.minecraft.world.finder.WorldObject;
import amidst.minecraft.world.finder.WorldObjectConsumer;
import amidst.minecraft.world.finder.WorldObjectProducer;
import amidst.preferences.BooleanPrefModel;

public abstract class IconLayer extends Layer {
	public IconLayer(LayerType layerType) {
		super(layerType);
	}

	@Override
	public boolean isVisible() {
		return getIsVisiblePreference().get();
	}

	public void generateMapObjects(Fragment fragment) {
		getProducer().produce(fragment.getCorner(),
				createWorldObjectConsumer(fragment));
	}

	private WorldObjectConsumer createWorldObjectConsumer(
			final Fragment fragment) {
		return new WorldObjectConsumer() {
			@Override
			public void consume(WorldObject worldObject) {
				fragment.addObject(new MapObject(worldObject, IconLayer.this));
			}
		};
	}

	protected abstract BooleanPrefModel getIsVisiblePreference();

	protected abstract WorldObjectProducer getProducer();
}
