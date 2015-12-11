package amidst.mojangapi.world.icon;

import java.util.LinkedList;
import java.util.List;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.player.MovablePlayerList;
import amidst.mojangapi.world.player.Player;

@ThreadSafe
public class PlayerProducer extends CachedWorldIconProducer {
	private final MovablePlayerList movablePlayerList;

	public PlayerProducer(RecognisedVersion recognisedVersion,
			MovablePlayerList movablePlayerList) {
		super(recognisedVersion);
		this.movablePlayerList = movablePlayerList;
	}

	@Override
	protected List<WorldIcon> doCreateCache() {
		List<WorldIcon> result = new LinkedList<WorldIcon>();
		for (Player player : movablePlayerList) {
			result.add(new WorldIcon(player.getPlayerCoordinates()
					.getCoordinatesInWorld(), player.getPlayerName(), player
					.getHead()));
		}
		return result;
	}
}
