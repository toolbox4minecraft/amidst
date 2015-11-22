package amidst.map.layer;

import amidst.Options;
import amidst.minecraft.world.finder.WorldObjectProducer;
import amidst.preferences.BooleanPrefModel;

public class OceanMonumentLayer extends IconLayer {
	@Override
	protected BooleanPrefModel getIsVisiblePreference() {
		return Options.instance.showOceanMonuments;
	}

	@Override
	protected WorldObjectProducer getProducer() {
		return getWorld().getOceanMonumentProducer();
	}
}
