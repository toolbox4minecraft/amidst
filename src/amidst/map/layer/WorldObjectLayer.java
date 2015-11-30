package amidst.map.layer;

import amidst.map.Fragment;
import amidst.map.Map;
import amidst.minecraft.world.object.WorldObjectProducer;
import amidst.preferences.PrefModel;

public class WorldObjectLayer extends Layer {
	private final WorldObjectProducer producer;

	public WorldObjectLayer(LayerType layerType,
			PrefModel<Boolean> isVisiblePreference, Map map,
			WorldObjectProducer producer) {
		super(layerType, isVisiblePreference, new WorldObjectDrawer(map,
				layerType));
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
}
