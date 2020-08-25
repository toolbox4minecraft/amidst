package amidst.mojangapi.file.nbt.player;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.NumberTag;
import net.querz.nbt.tag.Tag;

import amidst.documentation.Immutable;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.file.nbt.NBTTagKeys;
import amidst.mojangapi.file.nbt.NBTUtils;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.player.PlayerCoordinates;

@Immutable
public enum PlayerLocationLoader {
	;

	public static Optional<PlayerCoordinates> tryReadFromPlayerFile(Path file) throws IOException {
		try {
			return Optional.of(readPlayerCoordinates(NBTUtils.readTagFromFile(file)));
		} catch (NullPointerException | ClassCastException e) {
			AmidstLogger.warn(e, "cannot read player from file: {}", file);
			return Optional.empty();
		}
	}

	public static Optional<PlayerCoordinates> tryReadFromLevelDat(Path path) throws IOException {
		try {
			return Optional
					.of(readPlayerCoordinates(getSinglePlayerPlayerTag(getTagRootTag(NBTUtils.readTagFromFile(path)))));
		} catch (NullPointerException | ClassCastException e) {
			AmidstLogger.warn(e, "cannot read player from level.dat: {}", path);
			return Optional.empty();
		}
	}

	private static CompoundTag getTagRootTag(CompoundTag rootTag) {
		return rootTag.get(NBTTagKeys.TAG_KEY_DATA, CompoundTag.class);
	}

	private static CompoundTag getSinglePlayerPlayerTag(CompoundTag rootDataTag) {
		return rootDataTag.get(NBTTagKeys.TAG_KEY_PLAYER, CompoundTag.class);
	}

	private static PlayerCoordinates readPlayerCoordinates(CompoundTag tag) {
		Tag<?> dimensionTag = tag.get(NBTTagKeys.TAG_KEY_DIMENSION);
		Dimension dimension;
		if (dimensionTag instanceof NumberTag<?>) {
			dimension = Dimension.fromId(((NumberTag<?>) dimensionTag).asInt());
		} else {
			dimension = Dimension.fromName(dimensionTag.valueToString());
		}
		ListTag<?> posList = getTagPos(tag);
		return PlayerCoordinates.fromNBTFile(
				NBTUtils.getLongValue(posList.get(0)),
				NBTUtils.getLongValue(posList.get(1)),
				NBTUtils.getLongValue(posList.get(2)),
				dimension);
	}

	private static ListTag<?> getTagPos(CompoundTag tag) {
		return tag.get(NBTTagKeys.TAG_KEY_POS, ListTag.class);
	}
}
