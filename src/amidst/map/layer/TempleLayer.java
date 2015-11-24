package amidst.map.layer;

import amidst.Options;
import amidst.map.Map;
import amidst.minecraft.world.World;
import amidst.minecraft.world.finder.WorldObjectProducer;
import amidst.preferences.BooleanPrefModel;

public class TempleLayer extends IconLayer {
	public TempleLayer(World world, Map map) {
		super(world, map, LayerType.TEMPLE);
	}

	@Override
	protected BooleanPrefModel getIsVisiblePreference() {
		return Options.instance.showTemples;
	}

	@Override
	protected WorldObjectProducer getProducer() {
		return world.getTempleProducer();
	}
}
