package amidst.minecraft.world;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class MovablePlayerList implements Iterable<Player> {
	private static final MovablePlayerList EMPTY_INSTANCE = new MovablePlayerList(
			Collections.<Player> emptyList());

	public static MovablePlayerList empty() {
		return EMPTY_INSTANCE;
	}

	private final List<Player> players;

	public MovablePlayerList(List<Player> players) {
		this.players = Collections.unmodifiableList(players);
	}

	public boolean isEmpty() {
		return players.isEmpty();
	}

	public void savePlayerLocations() {
		for (Player player : players) {
			player.saveLocation();
		}
	}

	@Override
	public Iterator<Player> iterator() {
		return players.iterator();
	}
}
