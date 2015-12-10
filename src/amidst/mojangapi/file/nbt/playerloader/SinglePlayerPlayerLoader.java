package amidst.mojangapi.file.nbt.playerloader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.jnbt.CompoundTag;

import amidst.documentation.Immutable;
import amidst.mojangapi.file.directory.SaveDirectory;
import amidst.mojangapi.file.nbt.NBTTagKeys;
import amidst.mojangapi.world.Player;

@Immutable
public class SinglePlayerPlayerLoader extends PlayerLoader {
	private final SaveDirectory saveDirectory;

	public SinglePlayerPlayerLoader(SaveDirectory saveDirectory) {
		this.saveDirectory = saveDirectory;
	}

	@Override
	protected List<Player> doLoad() throws FileNotFoundException, IOException {
		return Arrays
				.asList(createNamelessPlayer(getSinglePlayerPlayerTag(getTagRootTag(saveDirectory
						.readLevelDat()))));
	}

	private CompoundTag getTagRootTag(CompoundTag rootTag) {
		return (CompoundTag) rootTag.getValue().get(NBTTagKeys.TAG_KEY_DATA);
	}

	private CompoundTag getSinglePlayerPlayerTag(CompoundTag rootDataTag) {
		return (CompoundTag) rootDataTag.getValue().get(
				NBTTagKeys.TAG_KEY_PLAYER);
	}
}
