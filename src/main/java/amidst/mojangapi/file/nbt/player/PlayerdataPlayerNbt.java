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
public class PlayerdataPlayerNbt extends PlayerNbt {
	private final SaveDirectory saveDirectory;
	private final String playerUUID;

	public PlayerdataPlayerNbt(SaveDirectory saveDirectory, String playerUUID) {
		this.saveDirectory = saveDirectory;
		this.playerUUID = playerUUID;
	}

	@Override
	protected boolean tryBackup() {
		return new AmidstBackupService().tryBackupPlayerdataFile(saveDirectory, playerUUID);
	}

	@Override
	protected void doWriteCoordinates(PlayerCoordinates coordinates) throws MojangApiParsingException {
		PlayerLocationSaver.writeToPlayerFile(coordinates, saveDirectory.getPlayerdataFile(playerUUID));
	}

	@Override
	public PlayerCoordinates readCoordinates() throws IOException, MojangApiParsingException {
		return PlayerLocationLoader
				.readFromPlayerFile(NBTUtils.readTagFromFile(saveDirectory.getPlayerdataFile(playerUUID)));
	}

	@Override
	public <R> R map(Supplier<R> ifIsLevelDat, Function<String, R> ifIsPlayerdata, Function<String, R> ifIsPlayers) {
		return ifIsPlayerdata.apply(playerUUID);
	}
}
