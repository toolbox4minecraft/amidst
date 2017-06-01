package amidst.mojangapi.world.player;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.ThreadSafe;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.file.PlayerInformationProvider;
import amidst.mojangapi.file.SaveGame;
import amidst.mojangapi.file.SaveGamePlayer;
import amidst.threading.WorkerExecutor;

@ThreadSafe
public class MovablePlayerList implements Iterable<Player> {
	private static final MovablePlayerList DUMMY = new MovablePlayerList(null, null, false, WorldPlayerType.NONE);

	public static MovablePlayerList dummy() {
		return DUMMY;
	}

	private final PlayerInformationProvider playerInformationProvider;
	private final SaveGame saveGame;
	private final boolean isSaveEnabled;

	private volatile WorldPlayerType worldPlayerType;
	private volatile ConcurrentLinkedQueue<Player> players = new ConcurrentLinkedQueue<>();

	public MovablePlayerList(
			PlayerInformationProvider playerInformationProvider,
			SaveGame saveGame,
			boolean isSaveEnabled,
			WorldPlayerType worldPlayerType) {
		this.playerInformationProvider = playerInformationProvider;
		this.saveGame = saveGame;
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
		return saveGame != null;
	}

	public void load(WorkerExecutor workerExecutor, Runnable onPlayerFinishedLoading) {
		if (saveGame != null) {
			AmidstLogger.info("loading player locations");
			ConcurrentLinkedQueue<Player> players = new ConcurrentLinkedQueue<>();
			this.players = players;
			loadPlayersLater(players, workerExecutor, onPlayerFinishedLoading);
		}
	}

	private void loadPlayersLater(
			ConcurrentLinkedQueue<Player> players,
			WorkerExecutor workerExecutor,
			Runnable onPlayerFinishedLoading) {
		workerExecutor.run(() -> loadPlayers(workerExecutor, players, onPlayerFinishedLoading));
	}

	@CalledOnlyBy(AmidstThread.WORKER)
	private void loadPlayers(
			WorkerExecutor workerExecutor,
			ConcurrentLinkedQueue<Player> players,
			Runnable onPlayerFinishedLoading) {
		for (SaveGamePlayer saveGamePlayer : worldPlayerType.tryReadPlayers(saveGame)) {
			workerExecutor.run(() -> loadPlayer(players, saveGamePlayer), onPlayerFinishedLoading);
		}
	}

	@CalledOnlyBy(AmidstThread.WORKER)
	private void loadPlayer(ConcurrentLinkedQueue<Player> players, SaveGamePlayer saveGamePlayer) {
		players.offer(new Player(saveGamePlayer.getPlayerInformation(playerInformationProvider), saveGamePlayer));
	}

	public boolean canSave() {
		return isSaveEnabled;
	}

	public void save() {
		if (isSaveEnabled) {
			AmidstLogger.info("saving player locations");
			for (Player player : players) {
				player.trySaveLocation();
			}
		} else {
			AmidstLogger.info("not saving player locations, because it is disabled for this version");
		}
	}

	@Override
	public Iterator<Player> iterator() {
		return players.iterator();
	}
}
