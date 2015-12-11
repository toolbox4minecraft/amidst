package amidst.mojangapi.file.nbt.playerfile;

import java.io.FileNotFoundException;
import java.io.IOException;

import amidst.documentation.Immutable;
import amidst.mojangapi.file.directory.SaveDirectory;
import amidst.mojangapi.file.nbt.NBTUtils;
import amidst.mojangapi.file.nbt.PlayerLocationLoader;
import amidst.mojangapi.file.nbt.PlayerLocationSaver;
import amidst.mojangapi.world.PlayerCoordinates;

@Immutable
public class PlayersPlayerFile extends PlayerFile {
	private final SaveDirectory saveDirectory;
	private final String playerName;

	public PlayersPlayerFile(SaveDirectory saveDirectory, String playerName) {
		this.saveDirectory = saveDirectory;
		this.playerName = playerName;
	}

	@Override
	protected boolean tryBackup() {
		return saveDirectory.tryBackupPlayersFile(playerName);
	}

	@Override
	protected void doWriteCoordinates(PlayerCoordinates coordinates)
			throws FileNotFoundException, IOException {
		PlayerLocationSaver.writeToPlayerFile(coordinates,
				saveDirectory.getPlayersFile(playerName));
	}

	@Override
	public PlayerCoordinates readCoordinates() throws FileNotFoundException,
			IOException {
		return PlayerLocationLoader.readFromPlayerFile(NBTUtils
				.readTagFromFile(saveDirectory.getPlayersFile(playerName)));
	}
}
