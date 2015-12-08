package amidst.mojangapi.world.loader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jnbt.CompoundTag;

import amidst.logging.Log;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.world.MovablePlayerList;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.WorldType;

public class WorldLoader {
	private PlayerMover playerMover;
	private PlayerLoader playerLoader;

	private File worldFile;
	private CompoundTag rootDataTag;
	private Exception exception;

	private long seed;
	public WorldType worldType;
	private String generatorOptions = "";
	private boolean isMultiPlayer;

	public WorldLoader(File file) {
		this.worldFile = getWorldFile(file);
		try {
			load();
		} catch (Exception e) {
			exception = e;
		}
	}

	private File getWorldFile(File file) {
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
		loadIsMultiPlayer(playersFolder, playerFiles);
		createPlayerMover();
		createPlayerLoader();
	}

	private void loadRootDataTag() throws IOException, FileNotFoundException {
		rootDataTag = getTagRootTag(NBTUtils.readTagFromFile(worldFile));
	}

	private void loadSeed() {
		seed = getTagRandomSeed();
	}

	private void loadGenerator() {
		if (hasTagGeneratorName()) {
			worldType = WorldType.from(getTagGeneratorName());
			if (worldType == WorldType.CUSTOMIZED) {
				generatorOptions = getTagGeneratorOptions();
			}
		} else {
			worldType = WorldType.DEFAULT;
		}
	}

	private void loadIsMultiPlayer(File playersFolder, File[] playerFiles) {
		isMultiPlayer = playersFolder.isDirectory() && playerFiles.length > 0;
		if (isMultiPlayer) {
			Log.i("Multiplayer world detected.");
		} else {
			Log.i("Singleplayer world detected.");
		}
	}

	private void createPlayerMover() {
		playerMover = new PlayerMover(worldFile, isMultiPlayer);
	}

	private void createPlayerLoader() {
		if (isMultiPlayer) {
			playerLoader = new MultiPlayerPlayerLoader(worldFile);
		} else {
			playerLoader = new SinglePlayerPlayerLoader(worldFile);
		}
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

	public boolean isLoadedSuccessfully() {
		return exception == null;
	}

	public Exception getException() {
		return exception;
	}

	public World get(MinecraftInterface minecraftInterface) {
		if (isLoadedSuccessfully()) {
			return World.file(minecraftInterface, seed, worldType,
					generatorOptions, isMultiPlayer,
					createMovablePlayerList(minecraftInterface));
		} else {
			return null;
		}
	}

	private MovablePlayerList createMovablePlayerList(
			MinecraftInterface minecraftInterface) {
		if (minecraftInterface.getRecognisedVersion().isSaveEnabled()) {
			return new MovablePlayerList(playerLoader, playerMover);
		} else {
			return new MovablePlayerList(playerLoader);
		}
	}
}
