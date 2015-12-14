package amidst.mojangapi.world.player;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.documentation.ThreadSafe;
import amidst.logging.Log;
import amidst.mojangapi.file.directory.SaveDirectory;
import amidst.mojangapi.file.nbt.playerfile.PlayerFile;
import amidst.threading.Worker;
import amidst.threading.WorkerExecutor;

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
		workerExecutor.invokeLater(new Worker<Void>() {
			@Override
			public Void execute() {
				List<PlayerFile> playerFiles = worldPlayerType
						.createPlayerFiles(saveDirectory);
				for (PlayerFile playerFile : playerFiles) {
					loadPlayerLater(players, playerFile, workerExecutor,
							onPlayerFinishedLoading);
				}
				return null;
			}

			@Override
			public void finished(Void result) {
				// noop
			}

			@Override
			public void error(Exception e) {
				// noop
			}
		});
	}

	private void loadPlayerLater(final ConcurrentLinkedQueue<Player> players,
			final PlayerFile playerFile, WorkerExecutor workerExecutor,
			final Runnable onPlayerFinishedLoading) {
		workerExecutor.invokeLater(new Worker<Void>() {
			@Override
			public Void execute() {
				Player player = playerFile.createPlayer(playerInformationCache);
				if (player.tryLoadLocation()) {
					players.offer(player);
				}
				return null;
			}

			@Override
			public void finished(Void result) {
				onPlayerFinishedLoading.run();
			}

			@Override
			public void error(Exception e) {
				// noop
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
