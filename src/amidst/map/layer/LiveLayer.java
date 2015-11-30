package amidst.map.layer;

import amidst.map.Fragment;
import amidst.map.Map;
import amidst.minecraft.world.World;

public abstract class LiveLayer extends Layer {
	public LiveLayer(World world, Map map, LayerType layerType) {
		super(world, map, layerType);
	}

	@Override
	public void load(Fragment fragment) {
		// noop
	}

	@Override
	public void reload(Fragment fragment) {
		// noop
	}
}
