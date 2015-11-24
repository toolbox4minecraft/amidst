package amidst.map.layer;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

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

	@Override
	public void load(Fragment fragment, int[] imageCache) {
		doLoad(fragment);
	}

	@Override
	public void reload(Fragment fragment, int[] imageCache) {
		fragment.removeMapObjects(getLayerType());
		doLoad(fragment);
	}

	@Override
	public void draw(Fragment fragment, Graphics2D g2d,
			AffineTransform layerMatrix) {
	}

	protected void doLoad(Fragment fragment) {
		getProducer().produce(fragment.getCorner(),
				createWorldObjectConsumer(fragment));
	}

	private WorldObjectConsumer createWorldObjectConsumer(
			final Fragment fragment) {
		return new WorldObjectConsumer() {
			@Override
			public void consume(WorldObject worldObject) {
				fragment.addMapObject(getLayerType(), new MapObject(
						worldObject, IconLayer.this));
			}
		};
	}

	protected abstract BooleanPrefModel getIsVisiblePreference();

	protected abstract WorldObjectProducer getProducer();
}
