package amidst.mojangapi.world.player;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import amidst.documentation.ThreadSafe;
import amidst.logging.Log;
import amidst.mojangapi.file.directory.SaveDirectory;

@ThreadSafe
public class MovablePlayerList implements Iterable<Player> {
	private static final MovablePlayerList DUMMY = new MovablePlayerList(null,
			false);

	public static MovablePlayerList dummy() {
		return DUMMY;
	}

	private final SaveDirectory saveDirectory;
	private final boolean isSaveEnabled;

	private volatile List<Player> players = Collections.emptyList();

	public MovablePlayerList(SaveDirectory saveDirectory, boolean isSaveEnabled) {
		this.saveDirectory = saveDirectory;
		this.isSaveEnabled = isSaveEnabled;
	}

	public boolean canLoad() {
		return saveDirectory != null;
	}

	public void load() {
		if (saveDirectory != null) {
			Log.i("loading player locations");
			List<Player> loadedPlayers = new LinkedList<Player>();
			List<Player> unloadedPlayers = saveDirectory
					.createMultiplayerPlayers();
			for (Player player : unloadedPlayers) {
				if (player.tryLoadLocation()) {
					loadedPlayers.add(player);
				}
			}
			this.players = Collections.unmodifiableList(loadedPlayers);
		} else {
			Log.i("cannot reload player locations");
		}
	}

	public boolean canSave() {
		return isSaveEnabled;
	}

	public void save() {
		if (isSaveEnabled) {
			Log.i("saving player locations");
			for (Player player : players) {
				player.trySaveLocation();
			}
		} else {
			Log.i("not saving player locations, because it is disabled for this version");
		}
	}

	@Override
	public Iterator<Player> iterator() {
		return players.iterator();
	}
}
