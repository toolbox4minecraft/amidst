package amidst.mojangapi.file.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.file.directory.SaveDirectory;
import amidst.mojangapi.file.nbt.LevelDatNbt;
import amidst.mojangapi.file.nbt.player.PlayerLocationLoader;
import amidst.mojangapi.file.nbt.player.PlayerLocationSaver;
import amidst.mojangapi.file.nbt.player.PlayerNbt;
import amidst.mojangapi.world.player.PlayerCoordinates;
import amidst.parsing.FormatException;

@Immutable
public class SaveDirectoryService {
	private final AmidstBackupService amidstBackupService = new AmidstBackupService();

	/**
	 * Returns a new valid instance of the class SaveDirectory. It tries to use
	 * the given file. If that is not valid it tires to use its parent file. If
	 * that is also not valid it will throw a FileNotFoundException.
	 */
	@NotNull
	public SaveDirectory newSaveDirectory(Path file) throws FileNotFoundException {
		Path currentFile = file;
		SaveDirectory result = null;
		if (currentFile == null) {
			// error
		} else {
			result = createValidSaveDirectory(currentFile);
			currentFile = currentFile.getParent();
			if (result != null) {
				return result;
			} else if (currentFile == null) {
				// error
			} else {
				result = createValidSaveDirectory(currentFile);
				currentFile = currentFile.getParent();
				if (result != null) {
					return result;
				} else {
					// error
				}
			}
		}
		throw new FileNotFoundException("unable to load save directory: " + file);
	}

	private SaveDirectory createValidSaveDirectory(Path currentFile) {
		SaveDirectory result = new SaveDirectory(currentFile);
		if (result.isValid()) {
			return result;
		} else {
			return null;
		}
	}

	public LevelDatNbt readLevelDat(SaveDirectory saveDirectory) throws IOException, FormatException {
		return LevelDatNbt.from(saveDirectory.getLevelDat());
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
	public Optional<PlayerNbt> tryReadSingleplayerPlayerNbt(SaveDirectory saveDirectory) {
		AmidstLogger.info("using player from level.dat");
		return tryReadCoordinatesFromLevelDat(saveDirectory).map(this::createLevelDatPlayerNbt);
	}

	/**
	 * Since version 1.7.6, minecraft stores players in the playerdata directory
	 * and uses the player uuid as filename.
	 */
	@NotNull
	public List<PlayerNbt> tryReadMultiplayerPlayerNbts(SaveDirectory saveDirectory) {
		List<PlayerNbt> playerdataPlayers = listFiles(saveDirectory.getPlayerdata())
				.filter(Files::isRegularFile)
				.map(f -> createPlayerdataPlayerNbt(saveDirectory, f))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toList());
		if (!playerdataPlayers.isEmpty()) {
			AmidstLogger.info("using players from the playerdata directory");
			return playerdataPlayers;
		} else {
			List<PlayerNbt> playersPlayers = listFiles(saveDirectory.getPlayers())
					.filter(Files::isRegularFile)
					.map(f -> createPlayersPlayerNbt(saveDirectory, f))
					.filter(Optional::isPresent)
					.map(Optional::get)
					.collect(Collectors.toList());
			if (!playersPlayers.isEmpty()) {
				AmidstLogger.info("using players from the players directory");
				return playersPlayers;
			} else {
				AmidstLogger.info("no multiplayer players found");
				return Collections.emptyList();
			}
		}
	}

	private Stream<Path> listFiles(Path directory) {
		if (Files.isDirectory(directory)) {
			try {
				return Files.list(directory);
			} catch (IOException e) {
				AmidstLogger.error(e, "Error while reading directory " + directory);
			}
		}
		return Stream.empty();
	}

	private Optional<PlayerNbt> createPlayerdataPlayerNbt(SaveDirectory saveDirectory, Path playerdataFile) {
		String playerUUID = getPlayerUUIDFromPlayerdataFile(playerdataFile);
		return tryReadCoordinatesFromPlayerdata(saveDirectory, playerUUID)
				.map(c -> createPlayerdataPlayerNbt(playerUUID, c));
	}

	private String getPlayerUUIDFromPlayerdataFile(Path playerdataFile) {
		return playerdataFile.getFileName().toString().split("\\.")[0];
	}

	private Optional<PlayerNbt> createPlayersPlayerNbt(SaveDirectory saveDirectory, Path playersFile) {
		String playerName = getPlayerNameFromPlayersFile(playersFile);
		return tryReadCoordinatesFromPlayers(saveDirectory, playerName).map(c -> createPlayersPlayerNbt(playerName, c));
	}

	private String getPlayerNameFromPlayersFile(Path playersFile) {
		return playersFile.getFileName().toString().split("\\.")[0];
	}

	private PlayerNbt createLevelDatPlayerNbt(PlayerCoordinates playerCoordinates) {
		return new PlayerNbt(playerCoordinates) {
			@Override
			public <R> R map(
					Supplier<R> ifIsLevelDat,
					Function<String, R> ifIsPlayerdata,
					Function<String, R> ifIsPlayers) {
				return ifIsLevelDat.get();
			}
		};
	}

	private PlayerNbt createPlayerdataPlayerNbt(String playerUUID, PlayerCoordinates playerCoordinates) {
		return new PlayerNbt(playerCoordinates) {
			@Override
			public <R> R map(
					Supplier<R> ifIsLevelDat,
					Function<String, R> ifIsPlayerdata,
					Function<String, R> ifIsPlayers) {
				return ifIsPlayerdata.apply(playerUUID);
			}
		};
	}

	private PlayerNbt createPlayersPlayerNbt(String playerName, PlayerCoordinates playerCoordinates) {
		return new PlayerNbt(playerCoordinates) {
			@Override
			public <R> R map(
					Supplier<R> ifIsLevelDat,
					Function<String, R> ifIsPlayerdata,
					Function<String, R> ifIsPlayers) {
				return ifIsPlayers.apply(playerName);
			}
		};
	}

	private Optional<PlayerCoordinates> tryReadCoordinatesFromLevelDat(SaveDirectory saveDirectory) {
		try {
			return PlayerLocationLoader.tryReadFromLevelDat(saveDirectory.getLevelDat());
		} catch (IOException e) {
			AmidstLogger.warn(e, "error while reading player coordinates from level.dat");
			return Optional.empty();
		}
	}

	private Optional<PlayerCoordinates> tryReadCoordinatesFromPlayerdata(
			SaveDirectory saveDirectory,
			String playerUUID) {
		try {
			return PlayerLocationLoader.tryReadFromPlayerFile(saveDirectory.getPlayerdataFile(playerUUID));
		} catch (IOException e) {
			AmidstLogger.warn(e, "error while reading player coordinates for player {}", playerUUID);
			return Optional.empty();
		}
	}

	private Optional<PlayerCoordinates> tryReadCoordinatesFromPlayers(SaveDirectory saveDirectory, String playerName) {
		try {
			return PlayerLocationLoader.tryReadFromPlayerFile(saveDirectory.getPlayersFile(playerName));
		} catch (IOException e) {
			AmidstLogger.warn(e, "error while reading player coordinates for player {}", playerName);
			return Optional.empty();
		}
	}

	public boolean tryBackup(SaveDirectory saveDirectory, PlayerNbt playerNbt) {
		return playerNbt.map(
				() -> amidstBackupService.tryBackupLevelDat(saveDirectory),
				playerUUID -> amidstBackupService.tryBackupPlayerdataFile(saveDirectory, playerUUID),
				playerName -> amidstBackupService.tryBackupPlayersFile(saveDirectory, playerName));
	}

	public boolean tryWriteCoordinates(
			SaveDirectory saveDirectory,
			PlayerNbt playerNbt,
			PlayerCoordinates coordinates) {
		return playerNbt.map(
				() -> tryWriteCoordinatesToLevelDat(saveDirectory, coordinates),
				playerUUID -> tryWriteCoordinatesToPlayerdata(saveDirectory, playerUUID, coordinates),
				playerName -> tryWriteCoordinatesToPlayers(saveDirectory, playerName, coordinates));
	}

	private boolean tryWriteCoordinatesToLevelDat(SaveDirectory saveDirectory, PlayerCoordinates coordinates) {
		try {
			return PlayerLocationSaver.tryWriteToLevelDat(coordinates, saveDirectory.getLevelDat());
		} catch (IOException e) {
			AmidstLogger.warn(e, "error while writing player coordinates to level.dat");
			return false;
		}
	}

	private boolean tryWriteCoordinatesToPlayerdata(
			SaveDirectory saveDirectory,
			String playerUUID,
			PlayerCoordinates coordinates) {
		try {
			return PlayerLocationSaver.tryWriteToPlayerFile(coordinates, saveDirectory.getPlayerdataFile(playerUUID));
		} catch (IOException e) {
			AmidstLogger.warn(e, "error while writing player coordinates for player {}", playerUUID);
			return false;
		}
	}

	private boolean tryWriteCoordinatesToPlayers(
			SaveDirectory saveDirectory,
			String playerName,
			PlayerCoordinates coordinates) {
		try {
			return PlayerLocationSaver.tryWriteToPlayerFile(coordinates, saveDirectory.getPlayersFile(playerName));
		} catch (IOException e) {
			AmidstLogger.warn(e, "error while writing player coordinates for player {}", playerName);
			return false;
		}
	}
}
