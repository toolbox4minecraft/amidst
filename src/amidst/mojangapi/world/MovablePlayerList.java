package amidst.mojangapi.world;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import amidst.mojangapi.world.loader.PlayerLoader;
import amidst.mojangapi.world.loader.PlayerMover;

public class MovablePlayerList implements Iterable<Player> {
	private static final MovablePlayerList EMPTY_INSTANCE = new MovablePlayerList();

	public static MovablePlayerList empty() {
		return EMPTY_INSTANCE;
	}

	private final PlayerLoader playerLoader;
	private final PlayerMover playerMover;

	private volatile List<Player> players;

	private MovablePlayerList() {
		this(PlayerLoader.dummy(), null);
	}

	public MovablePlayerList(PlayerLoader playerLoader) {
		this(playerLoader, null);
	}

	public MovablePlayerList(PlayerLoader playerLoader, PlayerMover playerMover) {
		this.playerLoader = playerLoader;
		this.playerMover = playerMover;
		reload();
	}

	public void reload() {
		this.players = Collections.unmodifiableList(playerLoader.load());
	}

	public boolean canSave() {
		return playerMover != null && !players.isEmpty();
	}

	public void save() {
		if (canSave()) {
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
