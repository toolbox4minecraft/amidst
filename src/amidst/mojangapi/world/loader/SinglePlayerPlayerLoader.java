package amidst.mojangapi.world.loader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.jnbt.CompoundTag;

import amidst.mojangapi.world.Player;

public class SinglePlayerPlayerLoader extends PlayerLoader {
	private static final String DEFAULT_SINGLE_PLAYER_PLAYER_NAME = "Player";

	private final File worldFile;

	public SinglePlayerPlayerLoader(File worldFile) {
		this.worldFile = worldFile;
	}

	@Override
	protected List<Player> doLoad() throws FileNotFoundException, IOException {
		return Arrays.asList(createPlayer(DEFAULT_SINGLE_PLAYER_PLAYER_NAME,
				getSinglePlayerPlayerTag(getTagRootTag(NBTUtils
						.readTagFromFile(worldFile)))));
	}

	private CompoundTag getTagRootTag(CompoundTag rootTag) {
		return (CompoundTag) rootTag.getValue().get(NBTTagKeys.TAG_KEY_DATA);
	}

	private CompoundTag getSinglePlayerPlayerTag(CompoundTag rootDataTag) {
		return (CompoundTag) rootDataTag.getValue().get(
				NBTTagKeys.TAG_KEY_PLAYER);
	}
}
