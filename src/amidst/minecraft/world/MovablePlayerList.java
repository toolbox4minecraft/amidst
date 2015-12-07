package amidst.minecraft.world;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class MovablePlayerList implements Iterable<Player> {
	private static final MovablePlayerList EMPTY_INSTANCE = new MovablePlayerList();

	public static MovablePlayerList empty() {
		return EMPTY_INSTANCE;
	}

	private final List<Player> players;
	private final PlayerMover playerMover;

	private MovablePlayerList() {
		this(Collections.<Player> emptyList(), null);
	}

	public MovablePlayerList(List<Player> players) {
		this(players, null);
	}

	public MovablePlayerList(List<Player> players, PlayerMover playerMover) {
		this.players = Collections.unmodifiableList(players);
		this.playerMover = playerMover;
	}

	public boolean canSavePlayerLocations() {
		return playerMover != null && !players.isEmpty();
	}

	public void savePlayerLocations() {
		if (canSavePlayerLocations()) {
			for (Player player : players) {
				if (player.getAndResetIsMoved()) {
					playerMover.movePlayer(player);
				}
			}
		}
	}

	@Override
	public Iterator<Player> iterator() {
		return players.iterator();
	}
}
