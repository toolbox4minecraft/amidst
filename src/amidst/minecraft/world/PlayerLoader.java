package amidst.minecraft.world;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jnbt.CompoundTag;
import org.jnbt.ListTag;
import org.jnbt.Tag;

import amidst.Util;
import amidst.logging.Log;
import amidst.map.MapObjectPlayer;

public class PlayerLoader {
	private static final String DEFAULT_SINGLE_PLAYER_PLAYER_NAME = "Player";

	// TODO: make non-static!
	public static WorldType genType = WorldType.DEFAULT;

	public static PlayerLoader newInstance(File file) {
		if (file.isDirectory()) {
			return new PlayerLoader(new File(file.getAbsoluteFile()
					+ "/level.dat"));
		} else {
			return new PlayerLoader(file);
		}
	}

	private NBTUtils nbtUtils = new NBTUtils();
	private List<MapObjectPlayer> players = new ArrayList<MapObjectPlayer>();

	private File file;

	private long seed;
	private boolean isMultiPlayerMap;
	private String generatorOptions = "";

	private PlayerLoader(File file) {
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
				.get(NBTTagKeys.TAG_KEY_DATA);
	}

	private void loadSeed(CompoundTag rootDataTag) {
		seed = (Long) nbtUtils.getValue(NBTTagKeys.TAG_KEY_RANDOM_SEED,
				rootDataTag);
	}

	private void loadGenerator(CompoundTag rootDataTag) {
		if (nbtUtils.isValueExisting(NBTTagKeys.TAG_KEY_GENERATOR_NAME,
				rootDataTag)) {
			genType = WorldType.from((String) nbtUtils.getValue(
					NBTTagKeys.TAG_KEY_GENERATOR_NAME, rootDataTag));
			if (genType == WorldType.CUSTOMIZED) {
				generatorOptions = (String) nbtUtils.getValue(
						NBTTagKeys.TAG_KEY_GENERATOR_OPTIONS, rootDataTag);
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
		return (CompoundTag) rootDataTag.getValue().get(
				NBTTagKeys.TAG_KEY_PLAYER);
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
		List<Tag> pos = ((ListTag) (ps.getValue().get(NBTTagKeys.TAG_KEY_POS)))
				.getValue();
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
