package amidst.mojangapi.file;

import amidst.documentation.Immutable;
import amidst.mojangapi.file.directory.SaveDirectory;
import amidst.mojangapi.file.nbt.player.PlayerNbt;
import amidst.mojangapi.file.service.SaveDirectoryService;
import amidst.mojangapi.world.player.PlayerCoordinates;
import amidst.mojangapi.world.player.PlayerInformation;

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

	public PlayerInformation getPlayerInformation(PlayerInformationProvider playerInformationProvider) {
		return playerNbt.map(
				() -> PlayerInformation.theSingleplayerPlayer(),
				playerUUID -> playerInformationProvider.getByPlayerUUID(playerUUID),
				playerName -> playerInformationProvider.getByPlayerName(playerName));
	}
}
