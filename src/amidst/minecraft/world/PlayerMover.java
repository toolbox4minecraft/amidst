package amidst.minecraft.world;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jnbt.CompoundTag;
import org.jnbt.DoubleTag;
import org.jnbt.ListTag;
import org.jnbt.Tag;

public class PlayerMover {
	private NBTUtils nbtUtils = new NBTUtils();
	private List<String> back = new ArrayList<String>();

	private boolean isMultiPlayerMap;
	private File file;

	public PlayerMover() {
		// TODO: initme
		this.isMultiPlayerMap = false;
		this.file = null;
	}

	public void movePlayer(String playerName, int x, int y) {
		File file = getPlayerFile(playerName);
		backupFile(file);
		if (backupSuccessful()) {
			movePlayer(file, x, y);
		}
	}

	// TODO: implement me!
	private boolean backupSuccessful() {
		throw new UnsupportedOperationException("implement me!");
	}

	private void movePlayer(File file, int x, int y) {
		if (isMultiPlayerMap) {
			try {
				movePlayerOnMultiPlayerMap(file, x, y);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				movePlayerOnSinglePlayerMap(file, x, y);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private File getPlayerFile(String playerName) {
		if (isMultiPlayerMap) {
			return getMultiPlayerPlayerFile(playerName);
		} else {
			return file;
		}
	}

	private File getMultiPlayerPlayerFile(String name) {
		return new File(file.getParent() + "/players/" + name + ".dat");
	}

	private void movePlayerOnMultiPlayerMap(File file, int x, int y)
			throws IOException, FileNotFoundException {
		CompoundTag dataTag = nbtUtils.readTagFromFile(file);
		CompoundTag modifiedDataTag = modifyPositionInDataTagMultiPlayer(
				dataTag, x, y);
		nbtUtils.writeTagToFile(file, modifiedDataTag);
	}

	private void movePlayerOnSinglePlayerMap(File file, int x, int y)
			throws IOException, FileNotFoundException {
		CompoundTag baseTag = nbtUtils.readTagFromFile(file);
		CompoundTag modifiedBaseTag = modifyPositionInBaseTagSinglePlayer(
				baseTag, x, y);
		nbtUtils.writeTagToFile(file, modifiedBaseTag);
	}

	private CompoundTag modifyPositionInBaseTagSinglePlayer(
			CompoundTag baseTag, int x, int y) {
		Map<String, Tag> baseMap = baseTag.getValue();
		Map<String, Tag> modifiedBaseMap = modifyPositionInBaseMapSinglePlayer(
				baseMap, x, y);
		return new CompoundTag(NBTTagKeys.TAG_KEY_BASE, modifiedBaseMap);
	}

	private Map<String, Tag> modifyPositionInBaseMapSinglePlayer(
			Map<String, Tag> baseMap, int x, int y) {
		Map<String, Tag> result = new HashMap<String, Tag>();
		CompoundTag dataTag = (CompoundTag) baseMap
				.get(NBTTagKeys.TAG_KEY_DATA);
		CompoundTag modifiedDataTag = modifyPositionInDataTagSinglePlayer(
				dataTag, x, y);
		result.put(NBTTagKeys.TAG_KEY_DATA, modifiedDataTag);
		return result;
	}

	private CompoundTag modifyPositionInDataTagSinglePlayer(
			CompoundTag dataTag, int x, int y) {
		Map<String, Tag> dataMap = dataTag.getValue();
		Map<String, Tag> modifiedDataMap = modifyPositionInDataMapSinglePlayer(
				dataMap, x, y);
		return new CompoundTag(NBTTagKeys.TAG_KEY_DATA, modifiedDataMap);
	}

	private Map<String, Tag> modifyPositionInDataMapSinglePlayer(
			Map<String, Tag> dataMap, int x, int y) {
		Map<String, Tag> result = new HashMap<String, Tag>(dataMap);
		CompoundTag playerTag = (CompoundTag) dataMap
				.get(NBTTagKeys.TAG_KEY_PLAYER);
		CompoundTag modifiedPlayerTag = modifyPositionInPlayerTagSinglePlayer(
				playerTag, x, y);
		result.put(NBTTagKeys.TAG_KEY_PLAYER, modifiedPlayerTag);
		return result;
	}

	private CompoundTag modifyPositionInPlayerTagSinglePlayer(
			CompoundTag playerTag, int x, int y) {
		Map<String, Tag> playerMap = playerTag.getValue();
		Map<String, Tag> modifiedPlayerMap = modifyPositionInPlayerMap(
				playerMap, x, y);
		return new CompoundTag(NBTTagKeys.TAG_KEY_PLAYER, modifiedPlayerMap);
	}

	private CompoundTag modifyPositionInDataTagMultiPlayer(CompoundTag dataTag,
			int x, int y) {
		Map<String, Tag> playerMap = dataTag.getValue();
		Map<String, Tag> modifiedPlayerMap = modifyPositionInPlayerMap(
				playerMap, x, y);
		return new CompoundTag(NBTTagKeys.TAG_KEY_DATA, modifiedPlayerMap);
	}

	private Map<String, Tag> modifyPositionInPlayerMap(
			Map<String, Tag> playerMap, int x, int y) {
		Map<String, Tag> result = new HashMap<String, Tag>(playerMap);
		ListTag posTag = (ListTag) playerMap.get(NBTTagKeys.TAG_KEY_POS);
		ListTag modifiedPosTag = modifyPositionInPosTag(posTag, x, y);
		result.put(NBTTagKeys.TAG_KEY_POS, modifiedPosTag);
		return result;
	}

	private ListTag modifyPositionInPosTag(ListTag posTag, int x, int y) {
		List<Tag> posList = posTag.getValue();
		List<Tag> modifiedPosList = modifyPositionInPosList(posList, x, y);
		return new ListTag(NBTTagKeys.TAG_KEY_POS, DoubleTag.class,
				modifiedPosList);
	}

	private List<Tag> modifyPositionInPosList(List<Tag> posList, int x, int y) {
		List<Tag> result = new ArrayList<Tag>(posList);
		result.set(0, new DoubleTag("x", x));
		result.set(1, new DoubleTag("y", 120));
		result.set(2, new DoubleTag("z", y));
		return result;
	}

	private void backupFile(File inputFile) {
		File backupFolder = new File(inputFile.getParentFile()
				+ "/amidst_backup/");
		if (!backupFolder.exists()) {
			backupFolder.mkdir();
		}

		File outputFile = new File(backupFolder + "/" + inputFile.getName());
		if (!back.contains(outputFile.toString())) {
			try {
				FileReader in = new FileReader(inputFile);
				FileWriter out = new FileWriter(outputFile);
				int c;

				while ((c = in.read()) != -1) {
					out.write(c);
				}

				in.close();
				out.close();
				back.add(outputFile.toString());
			} catch (Exception ignored) {
			}
		}
	}
}
