package MoF;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jnbt.CompoundTag;
import org.jnbt.DoubleTag;
import org.jnbt.ListTag;
import org.jnbt.NBTInputStream;
import org.jnbt.NBTOutputStream;
import org.jnbt.Tag;

import amidst.Util;
import amidst.logging.Log;
import amidst.map.MapObjectPlayer;

public class SaveLoader {
	public static enum WorldType {
		// @formatter:off
		DEFAULT("Default", "default"),
		FLAT("Flat", "flat"),
		LARGE_BIOMES("Large Biomes", "largeBiomes"),
		AMPLIFIED("Amplified", "amplified"),
		CUSTOMIZED("Customized", "customized");
		// @formatter:on

		public static WorldType from(String nameOrValue) {
			WorldType result = findInstance(nameOrValue);
			if (result == null) {
				Log.crash("Unable to find World Type: " + nameOrValue);
			}
			return result;
		}

		private static WorldType findInstance(String nameOrValue) {
			for (WorldType worldType : values()) {
				if (worldType.name.equalsIgnoreCase(nameOrValue)
						|| worldType.value.equalsIgnoreCase(nameOrValue)) {
					return worldType;
				}
			}
			return null;
		}

		private String name;
		private String value;

		private WorldType(String name, String value) {
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public String getValue() {
			return value;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	// @formatter:off
	public static final WorldType[] SELECTABLE_WORLD_TYPES = {
			WorldType.DEFAULT,
			WorldType.FLAT,
			WorldType.LARGE_BIOMES,
			WorldType.AMPLIFIED
	};
	// @formatter:on

	private static final String DEFAULT_SINGLE_PLAYER_PLAYER_NAME = "Player";

	private static final String TAG_KEY_POS = "Pos";
	private static final String TAG_KEY_PLAYER = "Player";
	private static final String TAG_KEY_RANDOM_SEED = "RandomSeed";
	private static final String TAG_KEY_GENERATOR_NAME = "generatorName";
	private static final String TAG_KEY_GENERATOR_OPTIONS = "generatorOptions";

	public static WorldType genType = WorldType.DEFAULT;

	public static SaveLoader newInstance(File file) {
		if (file.isDirectory()) {
			return new SaveLoader(new File(file.getAbsoluteFile()
					+ "/level.dat"));
		} else {
			return new SaveLoader(file);
		}
	}

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
		CompoundTag rootDataTag = getRootDataTag();
		loadSeed(rootDataTag);
		loadGenerator(rootDataTag);
		File playersFolder = getPlayersFolder();
		File[] playerFiles = getPlayerFiles(playersFolder);
		loadIsMultiPlayerMap(playersFolder, playerFiles);
		loadPlayers(rootDataTag, playerFiles);
	}

	private CompoundTag getRootDataTag() throws IOException,
			FileNotFoundException {
		return (CompoundTag) getTagFromFile(file).getValue().get("Data");
	}

	private void loadSeed(CompoundTag rootDataTag) {
		seed = (Long) getValue(TAG_KEY_RANDOM_SEED, rootDataTag);
	}

	private void loadGenerator(CompoundTag rootDataTag) {
		if (isValueExisting(TAG_KEY_GENERATOR_NAME, rootDataTag)) {
			genType = WorldType.from((String) getValue(TAG_KEY_GENERATOR_NAME,
					rootDataTag));
			if (genType == WorldType.CUSTOMIZED) {
				generatorOptions = (String) getValue(TAG_KEY_GENERATOR_OPTIONS,
						rootDataTag);
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
				addPlayer(getPlayerName(playerFile), getTagFromFile(playerFile));
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

	private CompoundTag getTagFromFile(File file) throws IOException,
			FileNotFoundException {
		NBTInputStream stream = createNBTInputStream(file);
		CompoundTag result = (CompoundTag) stream.readTag();
		stream.close();
		return result;
	}

	private NBTInputStream createNBTInputStream(File file) throws IOException,
			FileNotFoundException {
		return new NBTInputStream(new BufferedInputStream(new FileInputStream(
				file)));
	}

	private Object getValue(String key, CompoundTag rootDataTag) {
		return rootDataTag.getValue().get(key).getValue();
	}

	private boolean isValueExisting(String key, CompoundTag rootDataTag) {
		return rootDataTag.getValue().get(key) != null;
	}

	public void movePlayer(String name, int x, int y) {
		File out;
		if (isMultiPlayerMap) {
			String outPath = file.getParent() + "/players/" + name + ".dat";
			out = new File(outPath);
			backupFile(out);
			try {
				CompoundTag root = getTagFromFile(out);

				HashMap<String, Tag> rootMap = new HashMap<String, Tag>(
						root.getValue());
				ArrayList<Tag> posTag = new ArrayList<Tag>(
						((ListTag) rootMap.get(TAG_KEY_POS)).getValue());
				posTag.set(0, new DoubleTag("x", x));
				posTag.set(1, new DoubleTag("y", 120));
				posTag.set(2, new DoubleTag("z", y));
				rootMap.put(TAG_KEY_POS, new ListTag(TAG_KEY_POS,
						DoubleTag.class, posTag));
				root = new CompoundTag("Data", rootMap);
				NBTOutputStream outStream = new NBTOutputStream(
						new FileOutputStream(out));
				outStream.writeTag(root);
				outStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			out = file;
			backupFile(out);
			try {
				NBTInputStream inStream = createNBTInputStream(out);
				CompoundTag root = (CompoundTag) (((CompoundTag) inStream
						.readTag()).getValue().get("Data"));
				inStream.close();

				HashMap<String, Tag> rootMap = new HashMap<String, Tag>(
						root.getValue());
				HashMap<String, Tag> playerMap = new HashMap<String, Tag>(
						((CompoundTag) rootMap.get(TAG_KEY_PLAYER)).getValue());
				ArrayList<Tag> posTag = new ArrayList<Tag>(
						((ListTag) playerMap.get(TAG_KEY_POS)).getValue());
				posTag.set(0, new DoubleTag("x", x));
				posTag.set(1, new DoubleTag("y", 120));
				posTag.set(2, new DoubleTag("z", y));
				rootMap.put(TAG_KEY_PLAYER, new CompoundTag(TAG_KEY_PLAYER,
						playerMap));
				playerMap.put(TAG_KEY_POS, new ListTag(TAG_KEY_POS,
						DoubleTag.class, posTag));
				root = new CompoundTag("Data", rootMap);
				HashMap<String, Tag> base = new HashMap<String, Tag>();
				base.put("Data", root);
				root = new CompoundTag("Base", base);
				NBTOutputStream outStream = new NBTOutputStream(
						new FileOutputStream(out));
				outStream.writeTag(root);
				outStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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
