package amidst.map.layer;

import amidst.Options;
import amidst.map.Map;
import amidst.minecraft.world.World;
import amidst.minecraft.world.finder.WorldObjectProducer;
import amidst.preferences.BooleanPrefModel;

public class OceanMonumentLayer extends IconLayer {
	public OceanMonumentLayer(World world, Map map) {
		super(world, map, LayerType.OCEAN_MONUMENT);
	}

	@Override
	protected BooleanPrefModel getIsVisiblePreference() {
		return Options.instance.showOceanMonuments;
	}

	@Override
	protected WorldObjectProducer getProducer() {
		return world.getOceanMonumentProducer();
	}
}
