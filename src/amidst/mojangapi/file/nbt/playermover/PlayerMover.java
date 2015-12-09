package amidst.mojangapi.file.nbt.playermover;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jnbt.CompoundTag;
import org.jnbt.DoubleTag;
import org.jnbt.ListTag;
import org.jnbt.Tag;

import amidst.logging.Log;
import amidst.mojangapi.file.nbt.NBTTagKeys;
import amidst.mojangapi.file.nbt.NBTUtils;
import amidst.mojangapi.world.Player;
import amidst.mojangapi.world.PlayerCoordinates;

public abstract class PlayerMover {
	// TODO: gui feedback
	public void movePlayer(Player player) {
		PlayerCoordinates coordinates = player
				.getAndSetCurrentCoordinatesIfMoved();
		if (coordinates == null) {
			// noop
		} else if (tryBackup(player)) {
			try {
				doMovePlayer(player, coordinates);
			} catch (Exception e) {
				Log.w("Creation of backup file failed. Skipping player movement for player: "
						+ player.getPlayerName());
				e.printStackTrace();
			}
		} else {
			Log.w("Creation of backup file failed. Skipping player movement for player: "
					+ player.getPlayerName());
		}
	}

	protected abstract boolean tryBackup(Player player);

	protected abstract void doMovePlayer(Player player,
			PlayerCoordinates coordinates) throws FileNotFoundException,
			IOException;

	protected void movePlayerOnMultiPlayerWorld(PlayerCoordinates coordinates,
			File file) throws IOException, FileNotFoundException {
		CompoundTag dataTag = NBTUtils.readTagFromFile(file);
		CompoundTag modifiedDataTag = modifyPositionInDataTagMultiPlayer(
				dataTag, coordinates);
		NBTUtils.writeTagToFile(file, modifiedDataTag);
	}

	protected void movePlayerOnSinglePlayerWorld(PlayerCoordinates coordinates,
			File file) throws IOException, FileNotFoundException {
		CompoundTag baseTag = NBTUtils.readTagFromFile(file);
		CompoundTag modifiedBaseTag = modifyPositionInBaseTagSinglePlayer(
				baseTag, coordinates);
		NBTUtils.writeTagToFile(file, modifiedBaseTag);
	}

	private CompoundTag modifyPositionInBaseTagSinglePlayer(
			CompoundTag baseTag, PlayerCoordinates coordinates) {
		Map<String, Tag> baseMap = baseTag.getValue();
		Map<String, Tag> modifiedBaseMap = modifyPositionInBaseMapSinglePlayer(
				baseMap, coordinates);
		return new CompoundTag(NBTTagKeys.TAG_KEY_BASE, modifiedBaseMap);
	}

	private Map<String, Tag> modifyPositionInBaseMapSinglePlayer(
			Map<String, Tag> baseMap, PlayerCoordinates coordinates) {
		Map<String, Tag> result = new HashMap<String, Tag>();
		CompoundTag dataTag = (CompoundTag) baseMap
				.get(NBTTagKeys.TAG_KEY_DATA);
		CompoundTag modifiedDataTag = modifyPositionInDataTagSinglePlayer(
				dataTag, coordinates);
		result.put(NBTTagKeys.TAG_KEY_DATA, modifiedDataTag);
		return result;
	}

	private CompoundTag modifyPositionInDataTagSinglePlayer(
			CompoundTag dataTag, PlayerCoordinates coordinates) {
		Map<String, Tag> dataMap = dataTag.getValue();
		Map<String, Tag> modifiedDataMap = modifyPositionInDataMapSinglePlayer(
				dataMap, coordinates);
		return new CompoundTag(NBTTagKeys.TAG_KEY_DATA, modifiedDataMap);
	}

	private Map<String, Tag> modifyPositionInDataMapSinglePlayer(
			Map<String, Tag> dataMap, PlayerCoordinates coordinates) {
		Map<String, Tag> result = new HashMap<String, Tag>(dataMap);
		CompoundTag playerTag = (CompoundTag) dataMap
				.get(NBTTagKeys.TAG_KEY_PLAYER);
		CompoundTag modifiedPlayerTag = modifyPositionInPlayerTagSinglePlayer(
				playerTag, coordinates);
		result.put(NBTTagKeys.TAG_KEY_PLAYER, modifiedPlayerTag);
		return result;
	}

	private CompoundTag modifyPositionInPlayerTagSinglePlayer(
			CompoundTag playerTag, PlayerCoordinates coordinates) {
		Map<String, Tag> playerMap = playerTag.getValue();
		Map<String, Tag> modifiedPlayerMap = modifyPositionInPlayerMap(
				playerMap, coordinates);
		return new CompoundTag(NBTTagKeys.TAG_KEY_PLAYER, modifiedPlayerMap);
	}

	private CompoundTag modifyPositionInDataTagMultiPlayer(CompoundTag dataTag,
			PlayerCoordinates coordinates) {
		Map<String, Tag> playerMap = dataTag.getValue();
		Map<String, Tag> modifiedPlayerMap = modifyPositionInPlayerMap(
				playerMap, coordinates);
		return new CompoundTag(NBTTagKeys.TAG_KEY_DATA, modifiedPlayerMap);
	}

	private Map<String, Tag> modifyPositionInPlayerMap(
			Map<String, Tag> playerMap, PlayerCoordinates coordinates) {
		Map<String, Tag> result = new HashMap<String, Tag>(playerMap);
		ListTag posTag = (ListTag) playerMap.get(NBTTagKeys.TAG_KEY_POS);
		ListTag modifiedPosTag = modifyPositionInPosTag(posTag, coordinates);
		result.put(NBTTagKeys.TAG_KEY_POS, modifiedPosTag);
		return result;
	}

	private ListTag modifyPositionInPosTag(ListTag posTag,
			PlayerCoordinates coordinates) {
		List<Tag> posList = posTag.getValue();
		List<Tag> modifiedPosList = modifyPositionInPosList(posList,
				coordinates);
		return new ListTag(NBTTagKeys.TAG_KEY_POS, DoubleTag.class,
				modifiedPosList);
	}

	private List<Tag> modifyPositionInPosList(List<Tag> posList,
			PlayerCoordinates coordinates) {
		List<Tag> result = new ArrayList<Tag>(posList);
		result.set(0, new DoubleTag("x", coordinates.getX()));
		result.set(1, new DoubleTag("y", coordinates.getY()));
		result.set(2, new DoubleTag("z", coordinates.getZ()));
		return result;
	}
}
