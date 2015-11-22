package amidst.map.layer;

import amidst.Options;
import amidst.minecraft.world.finder.WorldObjectProducer;
import amidst.preferences.BooleanPrefModel;

public class PlayerLayer extends IconLayer {
	@Override
	protected BooleanPrefModel getIsVisiblePreference() {
		return Options.instance.showPlayers;
	}

	@Override
	protected WorldObjectProducer getProducer() {
		return getWorld().getPlayerProducer();
	}
}
