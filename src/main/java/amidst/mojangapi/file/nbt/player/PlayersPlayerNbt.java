package amidst.mojangapi.file.nbt.player;

import java.io.IOException;

import amidst.documentation.Immutable;
import amidst.mojangapi.file.MojangApiParsingException;
import amidst.mojangapi.file.directory.SaveDirectory;
import amidst.mojangapi.file.nbt.NBTUtils;
import amidst.mojangapi.world.player.Player;
import amidst.mojangapi.world.player.PlayerCoordinates;
import amidst.mojangapi.world.player.PlayerInformationCache;

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
		return saveDirectory.tryBackupPlayersFile(playerName);
	}

	@Override
	protected void doWriteCoordinates(PlayerCoordinates coordinates) throws MojangApiParsingException {
		PlayerLocationSaver.writeToPlayerFile(coordinates, saveDirectory.getPlayersFile(playerName));
	}

	@Override
	public PlayerCoordinates readCoordinates() throws IOException, MojangApiParsingException {
		return PlayerLocationLoader.readFromPlayerFile(NBTUtils.readTagFromFile(saveDirectory
				.getPlayersFile(playerName)));
	}

	@Override
	public Player createPlayer(PlayerInformationCache cache) {
		return new Player(cache.getByName(playerName), this);
	}
}
