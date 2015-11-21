package amidst.minecraft.world.finder;

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
	protected List<WorldObject> createCache() {
		if (world.isFileWorld()) {
			return createPlayerWorldObjects();
		} else {
			return Collections.emptyList();
		}
	}

	private List<WorldObject> createPlayerWorldObjects() {
		LinkedList<WorldObject> result = new LinkedList<WorldObject>();
		for (Player player : world.getAsFileWorld().getMovablePlayers()) {
			result.add(new WorldObject(player.getCoordinates(), player
					.getPlayerName(), player.getSkin()));
		}
		return result;
	}
}
