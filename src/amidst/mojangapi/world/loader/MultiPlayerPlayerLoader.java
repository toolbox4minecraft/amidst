package amidst.mojangapi.world.loader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import amidst.mojangapi.file.directory.SaveDirectory;
import amidst.mojangapi.world.Player;

public class MultiPlayerPlayerLoader extends PlayerLoader {
	private final SaveDirectory saveDirectory;

	public MultiPlayerPlayerLoader(SaveDirectory saveDirectory) {
		this.saveDirectory = saveDirectory;
	}

	@Override
	protected List<Player> doLoad() throws FileNotFoundException, IOException {
		List<Player> result = new ArrayList<Player>();
		for (File playerFile : saveDirectory.getPlayerFiles()) {
			if (playerFile.isFile()) {
				result.add(createPlayer(getPlayerName(playerFile),
						NBTUtils.readTagFromFile(playerFile)));
			}
		}
		return result;
	}

	private String getPlayerName(File playerFile) {
		return playerFile.getName().split("\\.")[0];
	}
}
