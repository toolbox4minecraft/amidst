package amidst.mojangapi.file.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.file.MojangApiParsingException;
import amidst.mojangapi.file.directory.SaveDirectory;
import amidst.mojangapi.file.nbt.LevelDatNbt;
import amidst.mojangapi.file.nbt.NBTUtils;
import amidst.mojangapi.file.nbt.player.LevelDatPlayerNbt;
import amidst.mojangapi.file.nbt.player.PlayerNbt;
import amidst.mojangapi.file.nbt.player.PlayerdataPlayerNbt;
import amidst.mojangapi.file.nbt.player.PlayersPlayerNbt;

@Immutable
public class SaveDirectoryService {
	/**
	 * Returns a new valid instance of the class SaveDirectory. It tries to use
	 * the given file. If that is not valid it tires to use its parent file. If
	 * that is also not valid it will throw a FileNotFoundException.
	 */
	@NotNull
	public SaveDirectory newSaveDirectory(File file) throws FileNotFoundException {
		File currentFile = file;
		SaveDirectory result = null;
		if (currentFile == null) {
			// error
		} else {
			result = createValidSaveDirectory(currentFile);
			currentFile = currentFile.getParentFile();
			if (result != null) {
				return result;
			} else if (currentFile == null) {
				// error
			} else {
				result = createValidSaveDirectory(currentFile);
				currentFile = currentFile.getParentFile();
				if (result != null) {
					return result;
				} else {
					// error
				}
			}
		}
		throw new FileNotFoundException("unable to load save directory: " + file);
	}

	private SaveDirectory createValidSaveDirectory(File currentFile) {
		SaveDirectory result = new SaveDirectory(currentFile);
		if (result.isValid()) {
			return result;
		} else {
			return null;
		}
	}

	public LevelDatNbt createLevelDat(SaveDirectory saveDirectory) throws IOException, MojangApiParsingException {
		return LevelDatNbt.from(NBTUtils.readTagFromFile(saveDirectory.getLevelDat()));
	}

	/**
	 * We need to let the user decide if he wants to load the singleplayer
	 * player from the level.dat file or if he wants to load the multiplayer
	 * players. That is, because a singleplayer map will have the playerdata
	 * directory. It contains information about each player that ever played on
	 * the map. However, if the map is loaded as singleplayer map, minecraft
	 * will use the information that is stored in the level.dat file. It will
	 * also overwrite the file in the playerdata directory that belongs to the
	 * player that loaded the map in singleplayer mode. So, if we change the
	 * player location in the playerdata directory it will just be ignored if
	 * the map is used as singleplayer map.
	 */
	@NotNull
	public List<PlayerNbt> createSingleplayerPlayerNbts(SaveDirectory saveDirectory) {
		AmidstLogger.info("using player from level.dat");
		return Arrays.asList(new LevelDatPlayerNbt(saveDirectory));
	}

	/**
	 * Since version 1.7.6, minecraft stores players in the playerdata directory
	 * and uses the player uuid as filename.
	 */
	@NotNull
	public List<PlayerNbt> createMultiplayerPlayerNbts(SaveDirectory saveDirectory) {
		List<PlayerNbt> result = new ArrayList<>();
		for (File playerdataFile : getPlayerdataFiles(saveDirectory)) {
			if (playerdataFile.isFile()) {
				result.add(new PlayerdataPlayerNbt(saveDirectory, getPlayerUUIDFromPlayerdataFile(playerdataFile)));
			}
		}
		if (!result.isEmpty()) {
			AmidstLogger.info("using players from the playerdata directory");
			return result;
		}
		for (File playersFile : getPlayersFiles(saveDirectory)) {
			if (playersFile.isFile()) {
				result.add(new PlayersPlayerNbt(saveDirectory, getPlayerNameFromPlayersFile(playersFile)));
			}
		}
		if (!result.isEmpty()) {
			AmidstLogger.info("using players from the players directory");
			return result;
		}
		AmidstLogger.info("no multiplayer players found");
		return result;
	}

	private File[] getPlayerdataFiles(SaveDirectory saveDirectory) {
		File[] files = saveDirectory.getPlayerdata().listFiles();
		if (files == null) {
			return new File[0];
		} else {
			return files;
		}
	}

	private File[] getPlayersFiles(SaveDirectory saveDirectory) {
		File[] files = saveDirectory.getPlayers().listFiles();
		if (files == null) {
			return new File[0];
		} else {
			return files;
		}
	}

	private String getPlayerUUIDFromPlayerdataFile(File playerdataFile) {
		return playerdataFile.getName().split("\\.")[0];
	}

	private String getPlayerNameFromPlayersFile(File playersFile) {
		return playersFile.getName().split("\\.")[0];
	}
}
