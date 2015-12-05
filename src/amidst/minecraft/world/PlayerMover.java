package amidst.minecraft.world;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jnbt.CompoundTag;
import org.jnbt.DoubleTag;
import org.jnbt.ListTag;
import org.jnbt.Tag;

import amidst.logging.Log;

public class PlayerMover {
	private File file;
	private boolean isMultiplayerWorld;

	public PlayerMover(File file, boolean isMultiplayerWorld) {
		this.file = file;
		this.isMultiplayerWorld = isMultiplayerWorld;
	}

	public void movePlayer(Player player) {
		File file = getPlayerFile(player);
		if (createBackup(file)) {
			doMovePlayer(player, file);
		} else {
			// TODO: gui feedback
			Log.w("Creation of backup file failed. Skipping player movement.");
		}
	}

	private boolean createBackup(File inputFile) {
		File backupFolder = getBackupFolder(inputFile);
		createIfNecessary(backupFolder);
		File outputFile = getOutputFile(backupFolder, inputFile);
		return doBackupFile(inputFile, outputFile);
	}

	private File getBackupFolder(File inputFile) {
		return new File(inputFile.getParentFile(), "amidst_backup");
	}

	private void createIfNecessary(File backupFolder) {
		if (!backupFolder.exists()) {
			backupFolder.mkdir();
		}
	}

	private File getOutputFile(File backupFolder, File inputFile) {
		return new File(backupFolder, inputFile.getName() + "_"
				+ System.currentTimeMillis());
	}

	private boolean doBackupFile(File inputFile, File outputFile) {
		try {
			Files.copy(inputFile.toPath(), outputFile.toPath());
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private void doMovePlayer(Player player, File file) {
		if (isMultiplayerWorld) {
			try {
				movePlayerOnMultiPlayerWorld(player, file);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				movePlayerOnSinglePlayerWorld(player, file);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private File getPlayerFile(Player player) {
		if (isMultiplayerWorld) {
			return getMultiPlayerPlayerFile(player.getPlayerName());
		} else {
			return file;
		}
	}

	private File getMultiPlayerPlayerFile(String playerName) {
		return new File(file.getParent() + "/players/" + playerName + ".dat");
	}

	private void movePlayerOnMultiPlayerWorld(Player player, File file)
			throws IOException, FileNotFoundException {
		CompoundTag dataTag = NBTUtils.readTagFromFile(file);
		CompoundTag modifiedDataTag = modifyPositionInDataTagMultiPlayer(
				dataTag, player);
		NBTUtils.writeTagToFile(file, modifiedDataTag);
	}

	private void movePlayerOnSinglePlayerWorld(Player player, File file)
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
		result.set(0, new DoubleTag("x", player.getCoordinates().getX()));
		result.set(1, new DoubleTag("y", 120));
		result.set(2, new DoubleTag("z", player.getCoordinates().getY()));
		return result;
	}
}
