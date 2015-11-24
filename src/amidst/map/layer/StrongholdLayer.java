package amidst.map.layer;

import amidst.Options;
import amidst.map.Map;
import amidst.minecraft.world.World;
import amidst.minecraft.world.finder.WorldObjectProducer;
import amidst.preferences.BooleanPrefModel;

public class StrongholdLayer extends IconLayer {
	public StrongholdLayer(World world, Map map) {
		super(world, map, LayerType.STRONGHOLD);
	}

	@Override
	protected BooleanPrefModel getIsVisiblePreference() {
		return Options.instance.showStrongholds;
	}

	@Override
	protected WorldObjectProducer getProducer() {
		return world.getStrongholdProducer();
	}
}
