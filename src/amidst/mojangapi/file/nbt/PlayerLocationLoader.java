package amidst.mojangapi.file.nbt;

import java.util.List;

import org.jnbt.CompoundTag;
import org.jnbt.ListTag;
import org.jnbt.Tag;

import amidst.documentation.Immutable;
import amidst.mojangapi.world.PlayerCoordinates;

@Immutable
public enum PlayerLocationLoader {
	;

	public static PlayerCoordinates readFromPlayerFile(CompoundTag file) {
		return readPlayerCoordinates(file);
	}

	public static PlayerCoordinates readFromLevelDat(CompoundTag file) {
		return readPlayerCoordinates(getSinglePlayerPlayerTag(getTagRootTag(file)));
	}

	private static CompoundTag getTagRootTag(CompoundTag rootTag) {
		return (CompoundTag) rootTag.getValue().get(NBTTagKeys.TAG_KEY_DATA);
	}

	private static CompoundTag getSinglePlayerPlayerTag(CompoundTag rootDataTag) {
		return (CompoundTag) rootDataTag.getValue().get(
				NBTTagKeys.TAG_KEY_PLAYER);

	}

	private static PlayerCoordinates readPlayerCoordinates(CompoundTag tag) {
		ListTag posTag = (ListTag) getTagPos(tag);
		List<Tag> posList = posTag.getValue();
		// @formatter:off
		return new PlayerCoordinates(
				(long) (double) (Double) posList.get(0).getValue(),
				(long) (double) (Double) posList.get(1).getValue(),
				(long) (double) (Double) posList.get(2).getValue());
		// @formatter:on
	}

	private static Tag getTagPos(CompoundTag tag) {
		return tag.getValue().get(NBTTagKeys.TAG_KEY_POS);
	}
}
