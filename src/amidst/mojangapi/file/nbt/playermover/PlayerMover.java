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

public abstract class PlayerMover {
	// TODO: gui feedback
	public void movePlayer(Player player) {
		if (tryBackup(player)) {
			try {
				doMovePlayer(player);
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

	protected abstract void doMovePlayer(Player player)
			throws FileNotFoundException, IOException;

	protected void movePlayerOnMultiPlayerWorld(Player player, File file)
			throws IOException, FileNotFoundException {
		CompoundTag dataTag = NBTUtils.readTagFromFile(file);
		CompoundTag modifiedDataTag = modifyPositionInDataTagMultiPlayer(
				dataTag, player);
		NBTUtils.writeTagToFile(file, modifiedDataTag);
	}

	protected void movePlayerOnSinglePlayerWorld(Player player, File file)
			throws IOException, FileNotFoundException {
		CompoundTag baseTag = NBTUtils.readTagFromFile(file);
		CompoundTag modifiedBaseTag = modifyPositionInBaseTagSinglePlayer(
				baseTag, player);
		NBTUtils.writeTagToFile(file, modifiedBaseTag);
	}

	private CompoundTag modifyPositionInBaseTagSinglePlayer(
			CompoundTag baseTag, Player player) {
		Map<String, Tag> baseMap = baseTag.getValue();
		Map<String, Tag> modifiedBaseMap = modifyPositionInBaseMapSinglePlayer(
				baseMap, player);
		return new CompoundTag(NBTTagKeys.TAG_KEY_BASE, modifiedBaseMap);
	}

	private Map<String, Tag> modifyPositionInBaseMapSinglePlayer(
			Map<String, Tag> baseMap, Player player) {
		Map<String, Tag> result = new HashMap<String, Tag>();
		CompoundTag dataTag = (CompoundTag) baseMap
				.get(NBTTagKeys.TAG_KEY_DATA);
		CompoundTag modifiedDataTag = modifyPositionInDataTagSinglePlayer(
				dataTag, player);
		result.put(NBTTagKeys.TAG_KEY_DATA, modifiedDataTag);
		return result;
	}

	private CompoundTag modifyPositionInDataTagSinglePlayer(
			CompoundTag dataTag, Player player) {
		Map<String, Tag> dataMap = dataTag.getValue();
		Map<String, Tag> modifiedDataMap = modifyPositionInDataMapSinglePlayer(
				dataMap, player);
		return new CompoundTag(NBTTagKeys.TAG_KEY_DATA, modifiedDataMap);
	}

	private Map<String, Tag> modifyPositionInDataMapSinglePlayer(
			Map<String, Tag> dataMap, Player player) {
		Map<String, Tag> result = new HashMap<String, Tag>(dataMap);
		CompoundTag playerTag = (CompoundTag) dataMap
				.get(NBTTagKeys.TAG_KEY_PLAYER);
		CompoundTag modifiedPlayerTag = modifyPositionInPlayerTagSinglePlayer(
				playerTag, player);
		result.put(NBTTagKeys.TAG_KEY_PLAYER, modifiedPlayerTag);
		return result;
	}

	private CompoundTag modifyPositionInPlayerTagSinglePlayer(
			CompoundTag playerTag, Player player) {
		Map<String, Tag> playerMap = playerTag.getValue();
		Map<String, Tag> modifiedPlayerMap = modifyPositionInPlayerMap(
				playerMap, player);
		return new CompoundTag(NBTTagKeys.TAG_KEY_PLAYER, modifiedPlayerMap);
	}

	private CompoundTag modifyPositionInDataTagMultiPlayer(CompoundTag dataTag,
			Player player) {
		Map<String, Tag> playerMap = dataTag.getValue();
		Map<String, Tag> modifiedPlayerMap = modifyPositionInPlayerMap(
				playerMap, player);
		return new CompoundTag(NBTTagKeys.TAG_KEY_DATA, modifiedPlayerMap);
	}

	private Map<String, Tag> modifyPositionInPlayerMap(
			Map<String, Tag> playerMap, Player player) {
		Map<String, Tag> result = new HashMap<String, Tag>(playerMap);
		ListTag posTag = (ListTag) playerMap.get(NBTTagKeys.TAG_KEY_POS);
		ListTag modifiedPosTag = modifyPositionInPosTag(posTag, player);
		result.put(NBTTagKeys.TAG_KEY_POS, modifiedPosTag);
		return result;
	}

	private ListTag modifyPositionInPosTag(ListTag posTag, Player player) {
		List<Tag> posList = posTag.getValue();
		List<Tag> modifiedPosList = modifyPositionInPosList(posList, player);
		return new ListTag(NBTTagKeys.TAG_KEY_POS, DoubleTag.class,
				modifiedPosList);
	}

	private List<Tag> modifyPositionInPosList(List<Tag> posList, Player player) {
		List<Tag> result = new ArrayList<Tag>(posList);
		result.set(0, new DoubleTag("x", player.getX()));
		result.set(1, new DoubleTag("y", player.getY()));
		result.set(2, new DoubleTag("z", player.getZ()));
		return result;
	}
}
