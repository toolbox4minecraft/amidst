package amidst.map.layer;

import amidst.Options;
import amidst.minecraft.world.finder.WorldObjectProducer;
import amidst.preferences.BooleanPrefModel;

public class VillageLayer extends IconLayer {
	public VillageLayer() {
		super(LayerType.VILLAGE);
	}

	@Override
	protected BooleanPrefModel getIsVisiblePreference() {
		return Options.instance.showVillages;
	}

	@Override
	protected WorldObjectProducer getProducer() {
		return getWorld().getVillageProducer();
	}
}
