package amidst.mojangapi.file.nbt.player;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.jnbt.CompoundTag;
import org.jnbt.IntTag;
import org.jnbt.ListTag;
import org.jnbt.Tag;

import amidst.documentation.Immutable;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.file.nbt.NBTTagKeys;
import amidst.mojangapi.file.nbt.NBTUtils;
import amidst.mojangapi.world.player.PlayerCoordinates;

@Immutable
public enum PlayerLocationLoader {
	;

	public static Optional<PlayerCoordinates> tryReadFromPlayerFile(File file) throws IOException {
		try {
			return Optional.of(readPlayerCoordinates(NBTUtils.readTagFromFile(file)));
		} catch (NullPointerException e) {
			AmidstLogger.warn(e, "cannot read player from file: " + file);
			return Optional.empty();
		}
	}

	public static Optional<PlayerCoordinates> tryReadFromLevelDat(File file) throws IOException {
		try {
			return Optional
					.of(readPlayerCoordinates(getSinglePlayerPlayerTag(getTagRootTag(NBTUtils.readTagFromFile(file)))));
		} catch (NullPointerException e) {
			AmidstLogger.warn(e, "cannot read player from level.dat: " + file);
			return Optional.empty();
		}
	}

	private static CompoundTag getTagRootTag(CompoundTag rootTag) {
		return (CompoundTag) rootTag.getValue().get(NBTTagKeys.TAG_KEY_DATA);
	}

	private static CompoundTag getSinglePlayerPlayerTag(CompoundTag rootDataTag) {
		return (CompoundTag) rootDataTag.getValue().get(NBTTagKeys.TAG_KEY_PLAYER);

	}

	private static PlayerCoordinates readPlayerCoordinates(CompoundTag tag) {
		int dimensionId = getTagDimension(tag).getValue();
		List<Tag> posList = getTagPos(tag).getValue();
		return PlayerCoordinates.fromNBTFile(
				(long) (double) (Double) posList.get(0).getValue(),
				(long) (double) (Double) posList.get(1).getValue(),
				(long) (double) (Double) posList.get(2).getValue(),
				dimensionId);
	}

	private static IntTag getTagDimension(CompoundTag tag) {
		return (IntTag) tag.getValue().get(NBTTagKeys.TAG_KEY_DIMENSION);
	}

	private static ListTag getTagPos(CompoundTag tag) {
		return (ListTag) tag.getValue().get(NBTTagKeys.TAG_KEY_POS);
	}
}
