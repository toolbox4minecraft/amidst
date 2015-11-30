package amidst.map.layer;

import amidst.map.Fragment;
import amidst.map.Map;
import amidst.minecraft.world.object.WorldObjectProducer;
import amidst.preferences.PrefModel;

public class WorldObjectLayer extends Layer {
	private final WorldObjectProducer producer;

	public WorldObjectLayer(Map map, LayerType layerType,
			PrefModel<Boolean> isVisiblePreference, WorldObjectProducer producer) {
		super(map, layerType, isVisiblePreference, new WorldObjectDrawer(map,
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
