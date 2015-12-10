package amidst.mojangapi.world;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import amidst.mojangapi.file.nbt.playerloader.PlayerLoader;
import amidst.mojangapi.file.nbt.playermover.PlayerMover;

public class MovablePlayerList implements Iterable<Player> {
	private static final MovablePlayerList DUMMY_INSTANCE = new MovablePlayerList();

	public static MovablePlayerList empty() {
		return DUMMY_INSTANCE;
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

	@Deprecated
	public boolean canReload() {
		return this != DUMMY_INSTANCE;
	}

	public void reload() {
		this.players = Collections.unmodifiableList(playerLoader.load());
	}

	public boolean canSave() {
		return playerMover != null;
	}

	public void save() {
		if (canSave()) {
			for (Player player : players) {
				playerMover.movePlayer(player);
			}
		}
	}

	@Override
	public Iterator<Player> iterator() {
		return players.iterator();
	}
}
