package amidst.mojangapi.file.nbt.player;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.UnaryOperator;

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.DoubleTag;
import net.querz.nbt.tag.ListTag;

import amidst.documentation.Immutable;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.file.nbt.NBTTagKeys;
import amidst.mojangapi.file.nbt.NBTUtils;
import amidst.mojangapi.world.player.PlayerCoordinates;

@Immutable
public enum PlayerLocationSaver {
	;

	public static boolean tryWriteToPlayerFile(PlayerCoordinates coordinates, Path file) throws IOException {
		try {
			CompoundTag dataTag = NBTUtils.readTagFromFile(file);
			CompoundTag modifiedDataTag = modifyPositionInMultiPlayer(dataTag, coordinates);
			NBTUtils.writeTagToFile(file, modifiedDataTag);
			return true;
		} catch (NullPointerException | ClassCastException e) {
			AmidstLogger.warn(e, "cannot write player to file: {}", file);
			return false;
		}
	}

	public static boolean tryWriteToLevelDat(PlayerCoordinates coordinates, Path path) throws IOException {
		try {
			CompoundTag dataTag = NBTUtils.readTagFromFile(path);
			CompoundTag modifiedDataTag = modifyPositionInSinglePlayer(dataTag, coordinates);
			NBTUtils.writeTagToFile(path, modifiedDataTag);
			return true;
		} catch (NullPointerException | ClassCastException e) {
			AmidstLogger.warn(e, "cannot write player to level.dat: {}", path);
			return false;
		}
	}

	private static CompoundTag modifyPositionInMultiPlayer(CompoundTag dataTag, PlayerCoordinates coordinates) {
		return modifyDataRoot(dataTag, root -> modifyPositionInPlayerTag(root, coordinates));
	}

	private static CompoundTag modifyPositionInSinglePlayer(CompoundTag dataTag, PlayerCoordinates coordinates) {
		return modifyDataRoot(dataTag, root -> {
			CompoundTag player = root.get(NBTTagKeys.TAG_KEY_PLAYER, CompoundTag.class);
			CompoundTag modifiedPlayer = modifyPositionInPlayerTag(player, coordinates);
			CompoundTag result = NBTUtils.shallowCopy(root);
			result.put(NBTTagKeys.TAG_KEY_PLAYER, modifiedPlayer);
			return result;
		});
	}

	private static CompoundTag modifyDataRoot(CompoundTag dataTag, UnaryOperator<CompoundTag> modifier) {
		CompoundTag root = dataTag.get(NBTTagKeys.TAG_KEY_DATA, CompoundTag.class);
		CompoundTag modifiedRoot = modifier.apply(root);
		CompoundTag result = NBTUtils.shallowCopy(dataTag);
		result.put(NBTTagKeys.TAG_KEY_DATA, modifiedRoot);
		return result;
	}

	private static CompoundTag modifyPositionInPlayerTag(CompoundTag dataTag, PlayerCoordinates coordinates) {
		CompoundTag result = NBTUtils.shallowCopy(dataTag);
		result.put(NBTTagKeys.TAG_KEY_POS, getPosListForCoordinates(coordinates));
		return result;
	}

	private static ListTag<?> getPosListForCoordinates(PlayerCoordinates coordinates) {
		ListTag<DoubleTag> posList = new ListTag<>(DoubleTag.class);
		posList.addDouble(coordinates.getXForNBTFile());
		posList.addDouble(coordinates.getYForNBTFile());
		posList.addDouble(coordinates.getZForNBTFile());
		return posList;
	}
}
