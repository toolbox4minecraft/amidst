package amidst.map.layer;

import amidst.Options;
import amidst.map.Map;
import amidst.minecraft.world.World;
import amidst.minecraft.world.finder.WorldObjectProducer;
import amidst.preferences.BooleanPrefModel;

public class SpawnLayer extends IconLayer {
	public SpawnLayer(World world, Map map) {
		super(world, map, LayerType.SPAWN);
	}

	@Override
	protected BooleanPrefModel getIsVisiblePreference() {
		return Options.instance.showSpawn;
	}

	@Override
	protected WorldObjectProducer getProducer() {
		return world.getSpawnProducer();
	}
}
