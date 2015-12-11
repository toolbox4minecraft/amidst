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
public class PlayerdataPlayerFile extends PlayerFile {
	private final SaveDirectory saveDirectory;
	private final String playerUUID;

	public PlayerdataPlayerFile(SaveDirectory saveDirectory, String playerName) {
		this.saveDirectory = saveDirectory;
		this.playerUUID = playerName;
	}

	@Override
	protected boolean tryBackup() {
		return saveDirectory.tryBackupPlayerdataFile(playerUUID);
	}

	@Override
	protected void doWriteCoordinates(PlayerCoordinates coordinates)
			throws FileNotFoundException, IOException {
		PlayerLocationSaver.writeToPlayerFile(coordinates,
				saveDirectory.getPlayerdataFile(playerUUID));
	}

	@Override
	public PlayerCoordinates readCoordinates() throws FileNotFoundException,
			IOException {
		return PlayerLocationLoader.readFromPlayerFile(NBTUtils
				.readTagFromFile(saveDirectory.getPlayerdataFile(playerUUID)));
	}
}
