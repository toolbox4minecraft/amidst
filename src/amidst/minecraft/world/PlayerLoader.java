package amidst.minecraft.world;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jnbt.CompoundTag;
import org.jnbt.ListTag;
import org.jnbt.Tag;

import amidst.logging.Log;
import amidst.map.MapObjectPlayer;

public class PlayerLoader {
	private static final String DEFAULT_SINGLE_PLAYER_PLAYER_NAME = "Player";

	private File worldFile;
	private CompoundTag rootDataTag;
	private Exception exception;

	private long seed;
	public WorldType generatorType;
	private String generatorOptions;
	private boolean isMultiPlayerMap;
	private List<MapObjectPlayer> players = new ArrayList<MapObjectPlayer>();

	public PlayerLoader(File file) {
		this.worldFile = getWorldFile(file);
		try {
			load();
		} catch (Exception e) {
			exception = e;
		}
	}

	public File getWorldFile(File file) {
		if (file.isDirectory()) {
			return new File(file.getAbsoluteFile() + "/level.dat");
		} else {
			return file;
		}
	}

	private void load() throws IOException, FileNotFoundException {
		loadRootDataTag();
		loadSeed();
		loadGenerator();
		File playersFolder = getPlayersFolder();
		File[] playerFiles = getPlayerFiles(playersFolder);
		loadIsMultiPlayerMap(playersFolder, playerFiles);
		loadPlayers(playerFiles);
	}

	private void loadRootDataTag() throws IOException, FileNotFoundException {
		rootDataTag = getTagRootTag(NBTUtils.readTagFromFile(worldFile));
	}

	private void loadSeed() {
		seed = getTagRandomSeed();
	}

	private void loadGenerator() {
		if (hasTagGeneratorName()) {
			generatorType = WorldType.from(getTagGeneratorName());
			if (generatorType == WorldType.CUSTOMIZED) {
				generatorOptions = getTagGeneratorOptions();
			}
		} else {
			generatorType = WorldType.DEFAULT;
			generatorOptions = "";
		}
	}

	private void loadIsMultiPlayerMap(File playersFolder, File[] playerFiles) {
		isMultiPlayerMap = playersFolder.exists() && playerFiles.length > 0;
	}

	private void loadPlayers(File[] playerFiles) throws IOException,
			FileNotFoundException {
		if (isMultiPlayerMap) {
			Log.i("Multiplayer map detected.");
			loadPlayersMultiPlayer(playerFiles);
		} else {
			Log.i("Singleplayer map detected.");
			loadPlayerSinglePlayer();
		}
	}

	private void loadPlayersMultiPlayer(File[] playerFiles) throws IOException,
			FileNotFoundException {
		for (File playerFile : playerFiles) {
			if (playerFile.isFile()) {
				addPlayer(getPlayerName(playerFile),
						NBTUtils.readTagFromFile(playerFile));
			}
		}
	}

	private void loadPlayerSinglePlayer() {
		addPlayer(DEFAULT_SINGLE_PLAYER_PLAYER_NAME, getSinglePlayerPlayerTag());
	}

	private void addPlayer(String playerName, CompoundTag tag) {
		ListTag posTag = (ListTag) getTagPos(tag);
		List<Tag> posList = posTag.getValue();
		double x = (Double) posList.get(0).getValue();
		double z = (Double) posList.get(2).getValue();
		players.add(new MapObjectPlayer(playerName, (int) x, (int) z));
	}

	private String getPlayerName(File playerFile) {
		return playerFile.getName().split("\\.")[0];
	}

	private File getPlayersFolder() {
		return new File(worldFile.getParent(), "players");
	}

	private File[] getPlayerFiles(File playersFolder) {
		File[] files = playersFolder.listFiles();
		if (files == null) {
			return new File[0];
		} else {
			return files;
		}
	}

	private CompoundTag getTagRootTag(CompoundTag rootTag) {
		return (CompoundTag) rootTag.getValue().get(NBTTagKeys.TAG_KEY_DATA);
	}

	private Long getTagRandomSeed() {
		return (Long) rootDataTag.getValue()
				.get(NBTTagKeys.TAG_KEY_RANDOM_SEED).getValue();
	}

	private boolean hasTagGeneratorName() {
		return rootDataTag.getValue().get(NBTTagKeys.TAG_KEY_GENERATOR_NAME) != null;
	}

	private String getTagGeneratorName() {
		return (String) rootDataTag.getValue()
				.get(NBTTagKeys.TAG_KEY_GENERATOR_NAME).getValue();
	}

	private String getTagGeneratorOptions() {
		return (String) rootDataTag.getValue()
				.get(NBTTagKeys.TAG_KEY_GENERATOR_OPTIONS).getValue();
	}

	private CompoundTag getSinglePlayerPlayerTag() {
		return (CompoundTag) rootDataTag.getValue().get(
				NBTTagKeys.TAG_KEY_PLAYER);
	}

	private Tag getTagPos(CompoundTag tag) {
		return tag.getValue().get(NBTTagKeys.TAG_KEY_POS);
	}

	public boolean isLoadedSuccessfully() {
		return exception == null;
	}

	public Exception getException() {
		return exception;
	}

	public long getSeed() {
		return seed;
	}

	public WorldType getGeneratorType() {
		return generatorType;
	}

	public String getGeneratorOptions() {
		return generatorOptions;
	}

	public boolean isMultiPlayerMap() {
		return isMultiPlayerMap;
	}

	public List<MapObjectPlayer> getPlayers() {
		return players;
	}
}
