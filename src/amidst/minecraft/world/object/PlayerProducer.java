package amidst.minecraft.world.object;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import amidst.minecraft.world.FileWorld.Player;
import amidst.minecraft.world.World;

public class PlayerProducer extends CachedWorldObjectProducer {
	public PlayerProducer(World world) {
		super(world);
	}

	@Override
	protected List<WorldIcon> createCache() {
		if (world.isFileWorld()) {
			return createPlayerWorldObjects();
		} else {
			return Collections.emptyList();
		}
	}

	private List<WorldIcon> createPlayerWorldObjects() {
		LinkedList<WorldIcon> result = new LinkedList<WorldIcon>();
		for (Player player : world.getAsFileWorld().getMovablePlayers()) {
			result.add(new WorldIcon(player.getCoordinates(), player
					.getPlayerName(), player.getSkin()));
		}
		return result;
	}
}
