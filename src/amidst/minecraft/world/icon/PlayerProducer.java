package amidst.minecraft.world.icon;

import java.util.LinkedList;
import java.util.List;

import amidst.minecraft.world.Player;
import amidst.minecraft.world.World;

public class PlayerProducer extends CachedWorldIconProducer {
	public PlayerProducer(World world) {
		super(world);
	}

	@Override
	protected List<WorldIcon> createCache() {
		List<WorldIcon> result = new LinkedList<WorldIcon>();
		for (Player player : world.getMovablePlayers()) {
			result.add(new WorldIcon(player.getCoordinates(), player
					.getPlayerName(), player.getSkin()));
		}
		return result;
	}
}
