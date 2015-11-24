package amidst.map.layer;

import amidst.Options;
import amidst.minecraft.world.finder.WorldObjectProducer;
import amidst.preferences.BooleanPrefModel;

public class TempleLayer extends IconLayer {
	public TempleLayer() {
		super(LayerType.TEMPLE);
	}

	@Override
	protected BooleanPrefModel getIsVisiblePreference() {
		return Options.instance.showTemples;
	}

	@Override
	protected WorldObjectProducer getProducer() {
		return getWorld().getTempleProducer();
	}
}
