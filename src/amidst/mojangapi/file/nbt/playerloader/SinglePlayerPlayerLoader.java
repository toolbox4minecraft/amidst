package amidst.mojangapi.file.nbt.playerloader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import amidst.documentation.Immutable;
import amidst.mojangapi.file.directory.SaveDirectory;
import amidst.mojangapi.file.nbt.PlayerLocationLoader;
import amidst.mojangapi.world.player.Player;

@Immutable
public class SinglePlayerPlayerLoader extends PlayerLoader {
	private final SaveDirectory saveDirectory;

	public SinglePlayerPlayerLoader(SaveDirectory saveDirectory) {
		this.saveDirectory = saveDirectory;
	}

	@Override
	protected List<Player> doLoad() throws FileNotFoundException, IOException {
		return Arrays.asList(Player.nameless(PlayerLocationLoader
				.readFromLevelDat(saveDirectory.readLevelDat())));
	}
}
