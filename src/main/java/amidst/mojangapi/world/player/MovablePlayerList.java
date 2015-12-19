package amidst.mojangapi.world.player;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.documentation.ThreadSafe;
import amidst.logging.Log;
import amidst.mojangapi.file.directory.SaveDirectory;
import amidst.mojangapi.file.nbt.playerfile.PlayerFile;
import amidst.threading.WorkerExecutor;
import amidst.threading.WorkerWithoutResult;

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
	private volatile ConcurrentLinkedQueue<Player> players = new ConcurrentLinkedQueue<Player>();

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

	public void load(WorkerExecutor workerExecutor,
			Runnable onPlayerFinishedLoading) {
		if (saveDirectory != null) {
			Log.i("loading player locations");
			ConcurrentLinkedQueue<Player> players = new ConcurrentLinkedQueue<Player>();
			this.players = players;
			loadPlayersLater(players, workerExecutor, onPlayerFinishedLoading);
		}
	}

	private void loadPlayersLater(final ConcurrentLinkedQueue<Player> players,
			final WorkerExecutor workerExecutor,
			final Runnable onPlayerFinishedLoading) {
		workerExecutor.invokeLater(new WorkerWithoutResult<PlayerFile>() {
			@Override
			protected void main() {
				for (PlayerFile playerFile : worldPlayerType
						.createPlayerFiles(saveDirectory)) {
					executeFork(playerFile);
				}
			}

			@Override
			protected void fork(PlayerFile playerFile) throws Exception {
				Player player = playerFile.createPlayer(playerInformationCache);
				if (player.tryLoadLocation()) {
					players.offer(player);
				}
			}

			@Override
			protected void onForkFinished() {
				onPlayerFinishedLoading.run();
			}
		});
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
