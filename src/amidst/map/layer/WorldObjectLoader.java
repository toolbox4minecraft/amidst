package amidst.map.layer;

import amidst.map.Fragment;
import amidst.minecraft.world.object.WorldObjectProducer;

public class WorldObjectLoader implements FragmentLoader {
	private final LayerType layerType;
	private final WorldObjectProducer producer;

	public WorldObjectLoader(LayerType layerType, WorldObjectProducer producer) {
		this.layerType = layerType;
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
