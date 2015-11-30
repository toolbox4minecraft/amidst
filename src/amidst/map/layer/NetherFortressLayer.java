package amidst.map.layer;

import amidst.Options;
import amidst.map.Map;
import amidst.minecraft.world.World;
import amidst.minecraft.world.object.WorldObjectProducer;
import amidst.preferences.BooleanPrefModel;

public class NetherFortressLayer extends IconLayer {
	public NetherFortressLayer(World world, Map map) {
		super(world, map, LayerType.NETHER_FORTRESS);
	}

	@Override
	protected BooleanPrefModel getIsVisiblePreference() {
		return Options.instance.showNetherFortresses;
	}

	@Override
	protected WorldObjectProducer getProducer() {
		return world.getNetherFortressProducer();
	}
}
