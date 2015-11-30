package amidst.map.layer;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import amidst.map.Fragment;
import amidst.map.Map;
import amidst.minecraft.world.object.WorldObjectProducer;
import amidst.preferences.PrefModel;

public class WorldObjectLayer extends Layer {
	private final WorldObjectProducer producer;

	public WorldObjectLayer(Map map, LayerType layerType,
			PrefModel<Boolean> isVisiblePreference, WorldObjectProducer producer) {
		super(map, layerType, isVisiblePreference);
		this.producer = producer;
	}

	@Override
	public void load(Fragment fragment) {
		doLoad(fragment);
	}

	@Override
	public void reload(Fragment fragment) {
		doLoad(fragment);
	}

	protected void doLoad(Fragment fragment) {
		fragment.putWorldObjects(layerType,
				producer.getAt(fragment.getCorner()));
	}

	@Override
	public void draw(Fragment fragment, Graphics2D g2d,
			AffineTransform layerMatrix) {
		new WorldObjectDrawer(map, layerType).draw(fragment, g2d, layerMatrix);
	}
}
