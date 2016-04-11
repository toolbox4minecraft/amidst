package amidst.mojangapi.file.nbt.player;

import java.util.List;

import org.jnbt.CompoundTag;
import org.jnbt.IntTag;
import org.jnbt.ListTag;
import org.jnbt.Tag;

import amidst.documentation.Immutable;
import amidst.mojangapi.file.MojangApiParsingException;
import amidst.mojangapi.file.nbt.NBTTagKeys;
import amidst.mojangapi.world.player.PlayerCoordinates;

@Immutable
public enum PlayerLocationLoader {
	;

	public static PlayerCoordinates readFromPlayerFile(CompoundTag file) throws MojangApiParsingException {
		try {
			return readPlayerCoordinates(file);
		} catch (NullPointerException e) {
			throw new MojangApiParsingException("cannot read player coordinates", e);
		}
	}

	public static PlayerCoordinates readFromLevelDat(CompoundTag file) throws MojangApiParsingException {
		try {
			return readPlayerCoordinates(getSinglePlayerPlayerTag(getTagRootTag(file)));
		} catch (NullPointerException e) {
			throw new MojangApiParsingException("cannot read player coordinates", e);
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
		// @formatter:off
		return PlayerCoordinates.fromNBTFile(
				(long) (double) (Double) posList.get(0).getValue(),
				(long) (double) (Double) posList.get(1).getValue(),
				(long) (double) (Double) posList.get(2).getValue(),
				dimensionId);
		// @formatter:on
	}

	private static IntTag getTagDimension(CompoundTag tag) {
		return (IntTag) tag.getValue().get(NBTTagKeys.TAG_KEY_DIMENSION);
	}

	private static ListTag getTagPos(CompoundTag tag) {
		return (ListTag) tag.getValue().get(NBTTagKeys.TAG_KEY_POS);
	}
}
