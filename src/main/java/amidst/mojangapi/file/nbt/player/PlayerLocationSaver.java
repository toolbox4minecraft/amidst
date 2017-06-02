package amidst.mojangapi.file.nbt.player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jnbt.CompoundTag;
import org.jnbt.DoubleTag;
import org.jnbt.ListTag;
import org.jnbt.Tag;

import amidst.documentation.Immutable;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.file.nbt.NBTTagKeys;
import amidst.mojangapi.file.nbt.NBTUtils;
import amidst.mojangapi.world.player.PlayerCoordinates;

@Immutable
public enum PlayerLocationSaver {
	;

	public static boolean tryWriteToPlayerFile(PlayerCoordinates coordinates, File file) throws IOException {
		try {
			CompoundTag dataTag = NBTUtils.readTagFromFile(file);
			CompoundTag modifiedDataTag = modifyPositionInDataTagMultiPlayer(dataTag, coordinates);
			NBTUtils.writeTagToFile(file, modifiedDataTag);
			return true;
		} catch (NullPointerException e) {
			AmidstLogger.warn(e, "cannot write player to file: " + file);
			return false;
		}
	}

	public static boolean tryWriteToLevelDat(PlayerCoordinates coordinates, File file) throws IOException {
		try {
			CompoundTag baseTag = NBTUtils.readTagFromFile(file);
			CompoundTag modifiedBaseTag = modifyPositionInBaseTagSinglePlayer(baseTag, coordinates);
			NBTUtils.writeTagToFile(file, modifiedBaseTag);
			return true;
		} catch (NullPointerException e) {
			AmidstLogger.warn(e, "cannot write player to level.dat: " + file);
			return false;
		}
	}

	private static CompoundTag modifyPositionInBaseTagSinglePlayer(CompoundTag baseTag, PlayerCoordinates coordinates) {
		Map<String, Tag> baseMap = baseTag.getValue();
		Map<String, Tag> modifiedBaseMap = modifyPositionInBaseMapSinglePlayer(baseMap, coordinates);
		return new CompoundTag(NBTTagKeys.TAG_KEY_BASE, modifiedBaseMap);
	}

	private static Map<String, Tag> modifyPositionInBaseMapSinglePlayer(
			Map<String, Tag> baseMap,
			PlayerCoordinates coordinates) {
		Map<String, Tag> result = new HashMap<>();
		CompoundTag dataTag = (CompoundTag) baseMap.get(NBTTagKeys.TAG_KEY_DATA);
		CompoundTag modifiedDataTag = modifyPositionInDataTagSinglePlayer(dataTag, coordinates);
		result.put(NBTTagKeys.TAG_KEY_DATA, modifiedDataTag);
		return result;
	}

	private static CompoundTag modifyPositionInDataTagSinglePlayer(CompoundTag dataTag, PlayerCoordinates coordinates) {
		Map<String, Tag> dataMap = dataTag.getValue();
		Map<String, Tag> modifiedDataMap = modifyPositionInDataMapSinglePlayer(dataMap, coordinates);
		return new CompoundTag(NBTTagKeys.TAG_KEY_DATA, modifiedDataMap);
	}

	private static Map<String, Tag> modifyPositionInDataMapSinglePlayer(
			Map<String, Tag> dataMap,
			PlayerCoordinates coordinates) {
		Map<String, Tag> result = new HashMap<>(dataMap);
		CompoundTag playerTag = (CompoundTag) dataMap.get(NBTTagKeys.TAG_KEY_PLAYER);
		CompoundTag modifiedPlayerTag = modifyPositionInPlayerTagSinglePlayer(playerTag, coordinates);
		result.put(NBTTagKeys.TAG_KEY_PLAYER, modifiedPlayerTag);
		return result;
	}

	private static CompoundTag modifyPositionInPlayerTagSinglePlayer(
			CompoundTag playerTag,
			PlayerCoordinates coordinates) {
		Map<String, Tag> playerMap = playerTag.getValue();
		Map<String, Tag> modifiedPlayerMap = modifyPositionInPlayerMap(playerMap, coordinates);
		return new CompoundTag(NBTTagKeys.TAG_KEY_PLAYER, modifiedPlayerMap);
	}

	private static CompoundTag modifyPositionInDataTagMultiPlayer(CompoundTag dataTag, PlayerCoordinates coordinates) {
		Map<String, Tag> playerMap = dataTag.getValue();
		Map<String, Tag> modifiedPlayerMap = modifyPositionInPlayerMap(playerMap, coordinates);
		return new CompoundTag(NBTTagKeys.TAG_KEY_DATA, modifiedPlayerMap);
	}

	private static Map<String, Tag> modifyPositionInPlayerMap(
			Map<String, Tag> playerMap,
			PlayerCoordinates coordinates) {
		Map<String, Tag> result = new HashMap<>(playerMap);
		ListTag posTag = (ListTag) playerMap.get(NBTTagKeys.TAG_KEY_POS);
		ListTag modifiedPosTag = modifyPositionInPosTag(posTag, coordinates);
		result.put(NBTTagKeys.TAG_KEY_POS, modifiedPosTag);
		return result;
	}

	private static ListTag modifyPositionInPosTag(ListTag posTag, PlayerCoordinates coordinates) {
		List<Tag> posList = posTag.getValue();
		List<Tag> modifiedPosList = modifyPositionInPosList(posList, coordinates);
		return new ListTag(NBTTagKeys.TAG_KEY_POS, DoubleTag.class, modifiedPosList);
	}

	private static List<Tag> modifyPositionInPosList(List<Tag> posList, PlayerCoordinates coordinates) {
		List<Tag> result = new ArrayList<>(posList);
		result.set(0, new DoubleTag("x", coordinates.getXForNBTFile()));
		result.set(1, new DoubleTag("y", coordinates.getYForNBTFile()));
		result.set(2, new DoubleTag("z", coordinates.getZForNBTFile()));
		return result;
	}
}
