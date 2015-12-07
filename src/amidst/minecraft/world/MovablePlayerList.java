package amidst.minecraft.world;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class MovablePlayerList implements Iterable<Player> {
	private static final MovablePlayerList EMPTY_INSTANCE = new MovablePlayerList(
			Collections.<Player> emptyList(), false);

	public static MovablePlayerList empty() {
		return EMPTY_INSTANCE;
	}

	private final List<Player> players;
	private final boolean canSavePlayerLocations;

	public MovablePlayerList(List<Player> players,
			boolean canSavePlayerLocations) {
		this.players = Collections.unmodifiableList(players);
		this.canSavePlayerLocations = canSavePlayerLocations;
	}

	public boolean canSavePlayerLocations() {
		return canSavePlayerLocations && !players.isEmpty();
	}

	public void savePlayerLocations() {
		if (canSavePlayerLocations) {
			for (Player player : players) {
				player.saveLocation();
			}
		}
	}

	@Override
	public Iterator<Player> iterator() {
		return players.iterator();
	}
}
