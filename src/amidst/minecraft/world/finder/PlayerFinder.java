package amidst.minecraft.world.finder;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import amidst.minecraft.world.FileWorld.Player;
import amidst.minecraft.world.World;

public class PlayerFinder extends CachedFinder {
	public PlayerFinder(World world) {
		super(world);
	}

	@Override
	protected List<Finding> createCache() {
		if (world.isFileWorld()) {
			return createPlayerFindings();
		} else {
			return Collections.emptyList();
		}
	}

	private List<Finding> createPlayerFindings() {
		LinkedList<Finding> result = new LinkedList<Finding>();
		for (Player player : world.getAsFileWorld().getPlayers()) {
			result.add(new Finding(player.getCoordinates(), player
					.getPlayerName(), player.getSkin()));
		}
		return result;
	}
}
