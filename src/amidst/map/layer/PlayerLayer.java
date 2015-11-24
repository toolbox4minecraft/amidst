package amidst.map.layer;

import amidst.Options;
import amidst.map.Map;
import amidst.minecraft.world.World;
import amidst.minecraft.world.finder.WorldObjectProducer;
import amidst.preferences.BooleanPrefModel;

public class PlayerLayer extends IconLayer {
	public PlayerLayer(World world, Map map) {
		super(world, map, LayerType.PLAYER);
	}

	@Override
	protected BooleanPrefModel getIsVisiblePreference() {
		return Options.instance.showPlayers;
	}

	@Override
	protected WorldObjectProducer getProducer() {
		return world.getPlayerProducer();
	}
}
