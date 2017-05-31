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
public class PlayersPlayerNbt extends PlayerNbt {
	private final SaveDirectory saveDirectory;
	private final String playerName;

	public PlayersPlayerNbt(SaveDirectory saveDirectory, String playerName) {
		this.saveDirectory = saveDirectory;
		this.playerName = playerName;
	}

	@Override
	protected boolean tryBackup() {
		return new AmidstBackupService().tryBackupPlayersFile(saveDirectory, playerName);
	}

	@Override
	protected void doWriteCoordinates(PlayerCoordinates coordinates) throws MojangApiParsingException {
		PlayerLocationSaver.writeToPlayerFile(coordinates, saveDirectory.getPlayersFile(playerName));
	}

	@Override
	public PlayerCoordinates readCoordinates() throws IOException, MojangApiParsingException {
		return PlayerLocationLoader
				.readFromPlayerFile(NBTUtils.readTagFromFile(saveDirectory.getPlayersFile(playerName)));
	}

	@Override
	public <R> R map(Supplier<R> ifIsLevelDat, Function<String, R> ifIsPlayerdata, Function<String, R> ifIsPlayers) {
		return ifIsPlayers.apply(playerName);
	}
}
