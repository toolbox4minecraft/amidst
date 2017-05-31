package amidst.mojangapi.file.nbt.player;

import java.io.IOException;
import java.util.function.Function;
import java.util.function.Supplier;

import amidst.documentation.Immutable;
import amidst.mojangapi.file.MojangApiParsingException;
import amidst.mojangapi.file.directory.SaveDirectory;
import amidst.mojangapi.file.nbt.NBTUtils;
import amidst.mojangapi.file.service.AmidstBackupService;
import amidst.mojangapi.world.player.PlayerCoordinates;

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
	public <R> R map(Supplier<R> ifIsLevelDat, Function<String, R> ifIsPlayerdata, Function<String, R> ifIsPlayers) {
		return ifIsLevelDat.get();
	}
}
