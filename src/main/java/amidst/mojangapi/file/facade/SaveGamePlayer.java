package amidst.mojangapi.file.facade;

import java.util.function.Function;
import java.util.function.Supplier;

import amidst.documentation.Immutable;
import amidst.mojangapi.file.directory.SaveDirectory;
import amidst.mojangapi.file.nbt.player.PlayerNbt;
import amidst.mojangapi.file.service.SaveDirectoryService;
import amidst.mojangapi.world.player.PlayerCoordinates;

@Immutable
public class SaveGamePlayer {
	private final SaveDirectoryService saveDirectoryService = new SaveDirectoryService();
	private final SaveDirectory saveDirectory;
	private final PlayerNbt playerNbt;

	public SaveGamePlayer(SaveDirectory saveDirectory, PlayerNbt playerNbt) {
		this.saveDirectory = saveDirectory;
		this.playerNbt = playerNbt;
	}

	public PlayerCoordinates getPlayerCoordinates() {
		return playerNbt.getPlayerCoordinates();
	}

	public boolean tryBackupAndWritePlayerCoordinates(PlayerCoordinates coordinates) {
		return saveDirectoryService.tryBackup(saveDirectory, playerNbt)
				&& saveDirectoryService.tryWriteCoordinates(saveDirectory, playerNbt, coordinates);
	}

	public <R> R map(Supplier<R> ifIsLevelDat, Function<String, R> ifIsPlayerdata, Function<String, R> ifIsPlayers) {
		return playerNbt.map(ifIsLevelDat, ifIsPlayerdata, ifIsPlayers);
	}
}
