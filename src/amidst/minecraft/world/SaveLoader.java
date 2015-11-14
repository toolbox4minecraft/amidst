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

import amidst.Util;
import amidst.logging.Log;
import amidst.map.MapObjectPlayer;

public class SaveLoader {
	private static final String DEFAULT_SINGLE_PLAYER_PLAYER_NAME = "Player";

	private static final String TAG_KEY_BASE = "Base";
	private static final String TAG_KEY_DATA = "Data";
	private static final String TAG_KEY_POS = "Pos";
	private static final String TAG_KEY_PLAYER = "Player";
	private static final String TAG_KEY_RANDOM_SEED = "RandomSeed";
	private static final String TAG_KEY_GENERATOR_NAME = "generatorName";
	private static final String TAG_KEY_GENERATOR_OPTIONS = "generatorOptions";

	// TODO: make non-static!
	public static WorldType genType = WorldType.DEFAULT;

	public static SaveLoader newInstance(File file) {
		if (file.isDirectory()) {
			return new SaveLoader(new File(file.getAbsoluteFile()
					+ "/level.dat"));
		} else {
			return new SaveLoader(file);
		}
	}

	private NBTUtils nbtUtils = new NBTUtils();
	private List<MapObjectPlayer> players = new ArrayList<MapObjectPlayer>();
	private List<String> back = new ArrayList<String>();

	private File file;

	private long seed;
	private boolean isMultiPlayerMap;
	private String generatorOptions = "";

	private SaveLoader(File file) {
		this.file = file;
		try {
			load();
		} catch (Exception e) {
			Util.showError(e);
		}
	}

	private void load() throws IOException, FileNotFoundException {
		CompoundTag rootDataTag = getRootDataTag(file);
		loadSeed(rootDataTag);
		loadGenerator(rootDataTag);
		File playersFolder = getPlayersFolder();
		File[] playerFiles = getPlayerFiles(playersFolder);
		loadIsMultiPlayerMap(playersFolder, playerFiles);
		loadPlayers(rootDataTag, playerFiles);
	}

	private CompoundTag getRootDataTag(File file) throws IOException,
			FileNotFoundException {
		return (CompoundTag) nbtUtils.readTagFromFile(file).getValue()
				.get(TAG_KEY_DATA);
	}

	private void loadSeed(CompoundTag rootDataTag) {
		seed = (Long) nbtUtils.getValue(TAG_KEY_RANDOM_SEED, rootDataTag);
	}

	private void loadGenerator(CompoundTag rootDataTag) {
		if (nbtUtils.isValueExisting(TAG_KEY_GENERATOR_NAME, rootDataTag)) {
			genType = WorldType.from((String) nbtUtils.getValue(
					TAG_KEY_GENERATOR_NAME, rootDataTag));
			if (genType == WorldType.CUSTOMIZED) {
				generatorOptions = (String) nbtUtils.getValue(
						TAG_KEY_GENERATOR_OPTIONS, rootDataTag);
			}
		}
	}

	private void loadIsMultiPlayerMap(File playersFolder, File[] playerFiles) {
		isMultiPlayerMap = playersFolder.exists() && playerFiles.length > 0;
	}

	private void loadPlayers(CompoundTag rootDataTag, File[] playerFiles)
			throws IOException, FileNotFoundException {
		if (isMultiPlayerMap) {
			Log.i("Multiplayer map detected.");
			loadPlayers(playerFiles);
		} else {
			Log.i("Singleplayer map detected.");
			addPlayer(DEFAULT_SINGLE_PLAYER_PLAYER_NAME,
					getSinglePlayerPlayerTag(rootDataTag));
		}
	}

	private CompoundTag getSinglePlayerPlayerTag(CompoundTag rootDataTag) {
		return (CompoundTag) rootDataTag.getValue().get(TAG_KEY_PLAYER);
	}

	private File[] getPlayerFiles(File playersFolder) {
		File[] files = playersFolder.listFiles();
		if (files == null) {
			return new File[0];
		} else {
			return files;
		}
	}

	private void loadPlayers(File[] playerFiles) throws IOException,
			FileNotFoundException {
		for (File playerFile : playerFiles) {
			if (playerFile.isFile()) {
				addPlayer(getPlayerName(playerFile),
						nbtUtils.readTagFromFile(playerFile));
			}
		}
	}

	private void addPlayer(String name, CompoundTag ps) {
		List<Tag> pos = ((ListTag) (ps.getValue().get(TAG_KEY_POS))).getValue();
		double x = (Double) pos.get(0).getValue();
		double z = (Double) pos.get(2).getValue();
		players.add(new MapObjectPlayer(name, (int) x, (int) z));
	}

	private String getPlayerName(File playerFile) {
		return playerFile.getName().split("\\.")[0];
	}

	private File getPlayersFolder() {
		return new File(file.getParent(), "players");
	}

	public void movePlayer(String name, int x, int y) {
		File out;
		if (isMultiPlayerMap) {
			String outPath = file.getParent() + "/players/" + name + ".dat";
			out = new File(outPath);
			backupFile(out);
			try {
				CompoundTag dataTag = nbtUtils.readTagFromFile(out);
				CompoundTag modifiedDataTag = modifyPositionInDataTagMultiPlayer(
						dataTag, x, y);
				nbtUtils.writeTagToFile(out, modifiedDataTag);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			out = file;
			backupFile(out);
			try {
				CompoundTag baseTag = nbtUtils.readTagFromFile(out);
				CompoundTag modifiedBaseTag = modifyPositionInBaseTag(baseTag,
						x, y);
				nbtUtils.writeTagToFile(out, modifiedBaseTag);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private CompoundTag modifyPositionInBaseTag(CompoundTag baseTag, int x,
			int y) {
		Map<String, Tag> baseMap = baseTag.getValue();
		Map<String, Tag> modifiedBaseMap = modifyPositionInBaseMap(baseMap, x,
				y);
		return new CompoundTag(TAG_KEY_BASE, modifiedBaseMap);
	}

	private Map<String, Tag> modifyPositionInBaseMap(Map<String, Tag> baseMap,
			int x, int y) {
		Map<String, Tag> result = new HashMap<String, Tag>();
		CompoundTag dataTag = (CompoundTag) baseMap.get(TAG_KEY_DATA);
		CompoundTag modifiedDataTag = modifyPositionInDataTagSinglePlayer(
				dataTag, x, y);
		result.put(TAG_KEY_DATA, modifiedDataTag);
		return result;
	}

	private CompoundTag modifyPositionInDataTagSinglePlayer(
			CompoundTag dataTag, int x, int y) {
		Map<String, Tag> dataMap = dataTag.getValue();
		Map<String, Tag> modifiedDataMap = modifyPositionInDataMap(dataMap, x,
				y);
		return new CompoundTag(TAG_KEY_DATA, modifiedDataMap);
	}

	private Map<String, Tag> modifyPositionInDataMap(Map<String, Tag> dataMap,
			int x, int y) {
		Map<String, Tag> result = new HashMap<String, Tag>(dataMap);
		CompoundTag playerTag = (CompoundTag) dataMap.get(TAG_KEY_PLAYER);
		CompoundTag modifiedPlayerTag = modifyPositionInPlayerTag(playerTag, x,
				y);
		result.put(TAG_KEY_PLAYER, modifiedPlayerTag);
		return result;
	}

	private CompoundTag modifyPositionInPlayerTag(CompoundTag playerTag, int x,
			int y) {
		Map<String, Tag> playerMap = playerTag.getValue();
		Map<String, Tag> modifiedPlayerMap = modifyPositionInPlayerMap(
				playerMap, x, y);
		return new CompoundTag(TAG_KEY_PLAYER, modifiedPlayerMap);
	}

	// MP
	private CompoundTag modifyPositionInDataTagMultiPlayer(CompoundTag dataTag,
			int x, int y) {
		Map<String, Tag> playerMap = dataTag.getValue();
		Map<String, Tag> modifiedPlayerMap = modifyPositionInPlayerMap(
				playerMap, x, y);
		return new CompoundTag(TAG_KEY_DATA, modifiedPlayerMap);
	}

	private Map<String, Tag> modifyPositionInPlayerMap(
			Map<String, Tag> playerMap, int x, int y) {
		Map<String, Tag> result = new HashMap<String, Tag>(playerMap);
		ListTag posTag = (ListTag) playerMap.get(TAG_KEY_POS);
		ListTag modifiedPosTag = modifyPositionInPosTag(posTag, x, y);
		result.put(TAG_KEY_POS, modifiedPosTag);
		return result;
	}

	private ListTag modifyPositionInPosTag(ListTag posTag, int x, int y) {
		List<Tag> posList = posTag.getValue();
		List<Tag> modifiedPosList = modifyPositionInPosList(posList, x, y);
		return new ListTag(TAG_KEY_POS, DoubleTag.class, modifiedPosList);
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

	public List<MapObjectPlayer> getPlayers() {
		return players;
	}

	public String getGeneratorOptions() {
		return generatorOptions;
	}

	public long getSeed() {
		return seed;
	}
}
