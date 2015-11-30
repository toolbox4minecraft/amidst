package amidst.map.layer;

import amidst.Options;
import amidst.map.Map;
import amidst.minecraft.world.World;
import amidst.minecraft.world.object.WorldObjectProducer;
import amidst.preferences.BooleanPrefModel;

public class VillageLayer extends IconLayer {
	public VillageLayer(World world, Map map) {
		super(world, map, LayerType.VILLAGE);
	}

	@Override
	protected BooleanPrefModel getIsVisiblePreference() {
		return Options.instance.showVillages;
	}

	@Override
	protected WorldObjectProducer getProducer() {
		return world.getVillageProducer();
	}
}
