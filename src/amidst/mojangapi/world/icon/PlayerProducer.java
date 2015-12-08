package amidst.mojangapi.world.icon;

import java.util.LinkedList;
import java.util.List;

import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.Player;
import amidst.mojangapi.world.World;

public class PlayerProducer extends CachedWorldIconProducer {
	public PlayerProducer(World world, RecognisedVersion recognisedVersion) {
		super(world, recognisedVersion);
	}

	@Override
	protected List<WorldIcon> createCache() {
		List<WorldIcon> result = new LinkedList<WorldIcon>();
		for (Player player : world.getMovablePlayerList()) {
			result.add(new WorldIcon(player.getCoordinates(), player
					.getPlayerName(), player.getSkin()));
		}
		return result;
	}
}
