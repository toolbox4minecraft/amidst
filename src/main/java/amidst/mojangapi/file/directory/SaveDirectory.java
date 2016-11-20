package amidst.mojangapi.file.directory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jnbt.CompoundTag;

import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.logging.Log;
import amidst.mojangapi.file.MojangApiParsingException;
import amidst.mojangapi.file.nbt.LevelDatNbt;
import amidst.mojangapi.file.nbt.NBTUtils;
import amidst.mojangapi.file.nbt.player.LevelDatPlayerNbt;
import amidst.mojangapi.file.nbt.player.PlayerNbt;
import amidst.mojangapi.file.nbt.player.PlayerdataPlayerNbt;
import amidst.mojangapi.file.nbt.player.PlayersPlayerNbt;

@Immutable
public class SaveDirectory {
	/**
	 * Returns a new valid instance of the class SaveDirectory. It tries to use
	 * the given file. If that is not valid it tires to use its parent file. If
	 * that is also not valid it will throw a FileNotFoundException.
	 */
	@NotNull
	public static SaveDirectory from(File file) throws FileNotFoundException {
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

	private static SaveDirectory createValidSaveDirectory(File currentFile) {
		SaveDirectory result = new SaveDirectory(currentFile);
		if (result.isValid()) {
			return result;
		} else {
			return null;
		}
	}

	private final File root;
	private final SaveAmidstDirectory amidst;
	private final File players;
	private final File playerdata;
	private final File levelDat;

	public SaveDirectory(File root) {
		this.root = root;
		this.amidst = new SaveAmidstDirectory(new File(root, "amidst"));
		this.players = new File(root, "players");
		this.playerdata = new File(root, "playerdata");
		this.levelDat = new File(root, "level.dat");
	}

	public boolean isValid() {
		return root.isDirectory() && levelDat.isFile();
	}

	public boolean hasMultiplayerPlayers() {
		return playerdata.isDirectory() || players.isDirectory();
	}

	public File getRoot() {
		return root;
	}

	public SaveAmidstDirectory getAmidst() {
		return amidst;
	}

	public File getPlayers() {
		return players;
	}

	public File getPlayerdata() {
		return playerdata;
	}

	public File getLevelDat() {
		return levelDat;
	}

	public File getPlayersFile(String playerName) {
		return new File(players, playerName + ".dat");
	}

	public File getPlayerdataFile(String playerUUID) {
		return new File(playerdata, playerUUID + ".dat");
	}

	public File[] getPlayersFiles() {
		File[] files = players.listFiles();
		if (files == null) {
			return new File[0];
		} else {
			return files;
		}
	}

	public File[] getPlayerdataFiles() {
		File[] files = playerdata.listFiles();
		if (files == null) {
			return new File[0];
		} else {
			return files;
		}
	}

	public CompoundTag readLevelDat() throws IOException {
		return NBTUtils.readTagFromFile(levelDat);
	}

	public LevelDatNbt createLevelDat() throws IOException, MojangApiParsingException {
		return new LevelDatNbt(readLevelDat());
	}

	public boolean tryBackupPlayersFile(String playerName) {
		return amidst.getBackup().tryBackupPlayersFile(getPlayersFile(playerName), playerName);
	}

	public boolean tryBackupPlayerdataFile(String playerUUID) {
		return amidst.getBackup().tryBackupPlayerdataFile(getPlayerdataFile(playerUUID), playerUUID);
	}

	public boolean tryBackupLevelDat() {
		return amidst.getBackup().tryBackupLevelDat(getLevelDat());
	}

	/**
	 * Since version 1.7.6, minecraft stores players in the playerdata directory
	 * and uses the player uuid as filename.
	 */
	@NotNull
	public List<PlayerNbt> createMultiplayerPlayerNbts() {
		List<PlayerNbt> result = new ArrayList<>();
		for (File playerdataFile : getPlayerdataFiles()) {
			if (playerdataFile.isFile()) {
				result.add(createPlayerdataPlayerNbt(getPlayerUUIDFromPlayerdataFile(playerdataFile)));
			}
		}
		if (!result.isEmpty()) {
			Log.i("using players from the playerdata directory");
			return result;
		}
		for (File playersFile : getPlayersFiles()) {
			if (playersFile.isFile()) {
				result.add(createPlayersPlayerNbt(getPlayerNameFromPlayersFile(playersFile)));
			}
		}
		if (!result.isEmpty()) {
			Log.i("using players from the players directory");
			return result;
		}
		Log.i("no multiplayer players found");
		return result;
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
	public List<PlayerNbt> createSingleplayerPlayerNbts() {
		Log.i("using player from level.dat");
		return Arrays.asList(createLevelDatPlayerNbt());
	}

	private PlayerNbt createLevelDatPlayerNbt() {
		return new LevelDatPlayerNbt(this);
	}

	private PlayerNbt createPlayerdataPlayerNbt(String playerUUID) {
		return new PlayerdataPlayerNbt(this, playerUUID);
	}

	private PlayerNbt createPlayersPlayerNbt(String playerName) {
		return new PlayersPlayerNbt(this, playerName);
	}

	private String getPlayerUUIDFromPlayerdataFile(File playerdataFile) {
		return playerdataFile.getName().split("\\.")[0];
	}

	private String getPlayerNameFromPlayersFile(File playersFile) {
		return playersFile.getName().split("\\.")[0];
	}
}
