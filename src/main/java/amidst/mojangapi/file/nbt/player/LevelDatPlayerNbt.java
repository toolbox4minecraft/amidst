package amidst.mojangapi.file.nbt.player;

import java.io.IOException;

import amidst.documentation.Immutable;
import amidst.mojangapi.file.AmidstBackupService;
import amidst.mojangapi.file.MojangApiParsingException;
import amidst.mojangapi.file.directory.SaveDirectory;
import amidst.mojangapi.file.nbt.NBTUtils;
import amidst.mojangapi.world.player.Player;
import amidst.mojangapi.world.player.PlayerCoordinates;
import amidst.mojangapi.world.player.PlayerInformation;
import amidst.mojangapi.world.player.PlayerInformationCache;

@Immutable
public class LevelDatPlayerNbt extends PlayerNbt {
	private final SaveDirectory saveDirectory;

	public LevelDatPlayerNbt(SaveDirectory saveDirectory) {
		this.saveDirectory = saveDirectory;
	}

	@Override
	protected boolean tryBackup() {
		return new AmidstBackupService().tryBackupLevelDat(saveDirectory);
	}

	@Override
	protected void doWriteCoordinates(PlayerCoordinates coordinates) throws MojangApiParsingException {
		PlayerLocationSaver.writeToLevelDat(coordinates, saveDirectory.getLevelDat());
	}

	@Override
	public PlayerCoordinates readCoordinates() throws IOException, MojangApiParsingException {
		return PlayerLocationLoader.readFromLevelDat(NBTUtils.readTagFromFile(saveDirectory.getLevelDat()));
	}

	@Override
	public Player createPlayer(PlayerInformationCache cache) {
		return new Player(PlayerInformation.theSingleplayerPlayer(), this);
	}
}
