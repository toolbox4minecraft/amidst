package amidst.mojangapi.world.player;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import amidst.documentation.ThreadSafe;
import amidst.logging.Log;
import amidst.mojangapi.file.directory.SaveDirectory;
import amidst.mojangapi.file.nbt.playerfile.PlayerFile;

@ThreadSafe
public class MovablePlayerList implements Iterable<Player> {
	private static final MovablePlayerList DUMMY = new MovablePlayerList(null,
			null, false, WorldPlayerType.NONE);

	public static MovablePlayerList dummy() {
		return DUMMY;
	}

	private final PlayerInformationCache playerInformationCache;
	private final SaveDirectory saveDirectory;
	private final boolean isSaveEnabled;

	private volatile WorldPlayerType worldPlayerType;
	private volatile List<Player> players = Collections.emptyList();

	public MovablePlayerList(PlayerInformationCache playerInformationCache,
			SaveDirectory saveDirectory, boolean isSaveEnabled,
			WorldPlayerType worldPlayerType) {
		this.playerInformationCache = playerInformationCache;
		this.saveDirectory = saveDirectory;
		this.isSaveEnabled = isSaveEnabled;
		this.worldPlayerType = worldPlayerType;
	}

	public WorldPlayerType getWorldPlayerType() {
		return worldPlayerType;
	}

	public void setWorldPlayerType(WorldPlayerType worldPlayerType) {
		this.worldPlayerType = worldPlayerType;
	}

	public boolean canLoad() {
		return saveDirectory != null;
	}

	public void load() {
		if (saveDirectory != null) {
			Log.i("loading player locations");
			List<Player> loadedPlayers = new LinkedList<Player>();
			List<PlayerFile> playerFiles = worldPlayerType
					.createPlayerFiles(saveDirectory);
			for (PlayerFile playerFile : playerFiles) {
				Player player = playerFile.createPlayer(playerInformationCache);
				if (player.tryLoadLocation()) {
					loadedPlayers.add(player);
				}
			}
			this.players = Collections.unmodifiableList(loadedPlayers);
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
