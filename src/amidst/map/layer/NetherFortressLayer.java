package amidst.map.layer;

import amidst.Options;
import amidst.minecraft.world.finder.WorldObjectProducer;
import amidst.preferences.BooleanPrefModel;

public class NetherFortressLayer extends IconLayer {
	public NetherFortressLayer() {
		super(LayerType.NETHER_FORTRESS);
	}

	@Override
	protected BooleanPrefModel getIsVisiblePreference() {
		return Options.instance.showNetherFortresses;
	}

	@Override
	protected WorldObjectProducer getProducer() {
		return getWorld().getNetherFortressProducer();
	}
}
