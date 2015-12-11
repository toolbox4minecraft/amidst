package amidst.mojangapi.file.nbt.playerfile;

import java.io.FileNotFoundException;
import java.io.IOException;

import amidst.documentation.Immutable;
import amidst.mojangapi.file.directory.SaveDirectory;
import amidst.mojangapi.file.nbt.PlayerLocationLoader;
import amidst.mojangapi.file.nbt.PlayerLocationSaver;
import amidst.mojangapi.world.PlayerCoordinates;

@Immutable
public class LevelDatPlayerFile extends PlayerFile {
	private final SaveDirectory saveDirectory;

	public LevelDatPlayerFile(SaveDirectory saveDirectory) {
		this.saveDirectory = saveDirectory;
	}

	@Override
	protected boolean tryBackup() {
		return saveDirectory.tryBackupLevelDat();
	}

	@Override
	protected void doWriteCoordinates(PlayerCoordinates coordinates)
			throws FileNotFoundException, IOException {
		PlayerLocationSaver.writeToLevelDat(coordinates,
				saveDirectory.getLevelDat());
	}

	@Override
	public PlayerCoordinates readCoordinates() throws FileNotFoundException,
			IOException {
		return PlayerLocationLoader.readFromLevelDat(saveDirectory
				.readLevelDat());
	}
}
