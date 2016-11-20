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
public class PlayerdataPlayerNbt extends PlayerNbt {
	private final SaveDirectory saveDirectory;
	private final String playerUUID;

	public PlayerdataPlayerNbt(SaveDirectory saveDirectory, String playerUUID) {
		this.saveDirectory = saveDirectory;
		this.playerUUID = playerUUID;
	}

	@Override
	protected boolean tryBackup() {
		return saveDirectory.tryBackupPlayerdataFile(playerUUID);
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
	public Player createPlayer(PlayerInformationCache cache) {
		return new Player(cache.getByUUID(playerUUID), this);
	}
}
