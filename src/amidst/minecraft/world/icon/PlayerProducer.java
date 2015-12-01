package amidst.minecraft.world.icon;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import amidst.minecraft.world.FileWorld.Player;
import amidst.minecraft.world.World;

public class PlayerProducer extends CachedWorldIconProducer {
	public PlayerProducer(World world) {
		super(world);
	}

	@Override
	protected List<WorldIcon> createCache() {
		if (world.isFileWorld()) {
			return createPlayerWorldIcons();
		} else {
			return Collections.emptyList();
		}
	}

	private List<WorldIcon> createPlayerWorldIcons() {
		LinkedList<WorldIcon> result = new LinkedList<WorldIcon>();
		for (Player player : world.getAsFileWorld().getMovablePlayers()) {
			result.add(new WorldIcon(player.getCoordinates(), player
					.getPlayerName(), player.getSkin()));
		}
		return result;
	}
}
