package amidst.mojangapi.world.loader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import amidst.mojangapi.world.Player;

public class MultiPlayerPlayerLoader extends PlayerLoader {
	private final File worldFile;

	public MultiPlayerPlayerLoader(File worldFile) {
		this.worldFile = worldFile;
	}

	@Override
	protected List<Player> doLoad() throws FileNotFoundException, IOException {
		List<Player> result = new ArrayList<Player>();
		for (File playerFile : getPlayerFiles(getPlayersFolder())) {
			if (playerFile.isFile()) {
				result.add(createPlayer(getPlayerName(playerFile),
						NBTUtils.readTagFromFile(playerFile)));
			}
		}
		return result;
	}

	private File[] getPlayerFiles(File playersFolder) {
		File[] files = playersFolder.listFiles();
		if (files == null) {
			return new File[0];
		} else {
			return files;
		}
	}

	private File getPlayersFolder() {
		return new File(worldFile.getParent(), "players");
	}

	private String getPlayerName(File playerFile) {
		return playerFile.getName().split("\\.")[0];
	}
}
