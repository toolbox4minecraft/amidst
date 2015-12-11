package amidst.mojangapi.file.nbt.playerloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import amidst.documentation.Immutable;
import amidst.mojangapi.file.directory.SaveDirectory;
import amidst.mojangapi.file.nbt.NBTUtils;
import amidst.mojangapi.file.nbt.PlayerLocationLoader;
import amidst.mojangapi.world.Player;

@Immutable
public class MultiPlayerPlayerLoader extends PlayerLoader {
	private final SaveDirectory saveDirectory;

	public MultiPlayerPlayerLoader(SaveDirectory saveDirectory) {
		this.saveDirectory = saveDirectory;
	}

	@Override
	protected List<Player> doLoad() throws FileNotFoundException, IOException {
		List<Player> result = new ArrayList<Player>();
		for (File playerFile : saveDirectory.getPlayersFiles()) {
			if (playerFile.isFile()) {
				String playerName = getPlayerName(playerFile);
				result.add(Player.named(playerName, PlayerLocationLoader
						.readFromPlayerFile(NBTUtils
								.readTagFromFile(playerFile))));
			}
		}
		return result;
	}

	private String getPlayerName(File playerFile) {
		return playerFile.getName().split("\\.")[0];
	}
}
