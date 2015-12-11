package amidst.mojangapi.file.directory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jnbt.CompoundTag;

import amidst.documentation.Immutable;
import amidst.logging.Log;
import amidst.mojangapi.file.json.PlayerInformationRetriever;
import amidst.mojangapi.file.nbt.LevelDat;
import amidst.mojangapi.file.nbt.NBTUtils;
import amidst.mojangapi.file.nbt.playerfile.LevelDatPlayerFile;
import amidst.mojangapi.file.nbt.playerfile.PlayerdataPlayerFile;
import amidst.mojangapi.file.nbt.playerfile.PlayersPlayerFile;
import amidst.mojangapi.world.player.Player;

@Immutable
public class SaveDirectory {
	/**
	 * Returns a new valid instance of the class SaveDirectory. It tries to use
	 * the given file. If that is not valid it tires to use its parent file. If
	 * that is also not valid it will throw a FileNotFoundException.
	 * 
	 * @return The SaveDirectory, but never null.
	 * @throws FileNotFoundException
	 */
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
		throw new FileNotFoundException("unable to load save directory: "
				+ file);
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
	private final File players;
	private final File playerdata;
	private final File levelDat;
	private final File backupRoot;
	private final File backupPlayers;
	private final File backupPlayerdata;

	public SaveDirectory(File root) {
		this.root = root;
		this.players = new File(root, "players");
		this.playerdata = new File(root, "playerdata");
		this.levelDat = new File(root, "level.dat");
		this.backupRoot = new File(root, "amidst_backup");
		this.backupPlayers = new File(backupRoot, "players");
		this.backupPlayerdata = new File(backupRoot, "playerdata");
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

	public File getPlayers() {
		return players;
	}

	// TODO: use me!
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

	public File getBackupLevelDat() {
		return new File(backupRoot, "level.dat" + millis());
	}

	public File getBackupPlayersFile(String playerName) {
		return new File(backupPlayers, playerName + ".dat" + millis());
	}

	public File getBackupPlayerdataFile(String playerUUID) {
		return new File(backupPlayerdata, playerUUID + ".dat" + millis());
	}

	// TODO: switch to more readable timestamp
	private String millis() {
		return "_" + System.currentTimeMillis();
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

	public CompoundTag readLevelDat() throws FileNotFoundException, IOException {
		return NBTUtils.readTagFromFile(levelDat);
	}

	public LevelDat createLevelDat() throws FileNotFoundException, IOException {
		return new LevelDat(readLevelDat());
	}

	public boolean tryBackupLevelDat() {
		File backupFile = getBackupLevelDat();
		return ensureDirectoryExists(backupRoot)
				&& tryCopy(getLevelDat(), backupFile) && backupFile.isFile();
	}

	public boolean tryBackupPlayersFile(String playerName) {
		File backupFile = getBackupPlayersFile(playerName);
		return ensureDirectoryExists(backupPlayers)
				&& tryCopy(getPlayersFile(playerName), backupFile)
				&& backupFile.isFile();
	}

	public boolean tryBackupPlayerdataFile(String playerUUID) {
		File backupFile = getBackupPlayerdataFile(playerUUID);
		return ensureDirectoryExists(backupPlayerdata)
				&& tryCopy(getPlayerdataFile(playerUUID), backupFile)
				&& backupFile.isFile();
	}

	private boolean ensureDirectoryExists(File directory) {
		if (!directory.exists()) {
			directory.mkdirs();
		}
		return directory.isDirectory();
	}

	private boolean tryCopy(File from, File to) {
		try {
			Files.copy(from.toPath(), to.toPath());
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public Player createLevelDatPlayer() {
		return Player.nameless(new LevelDatPlayerFile(this));
	}

	public Player createPlayerdataPlayer(String playerName, String playerUUID) {
		return Player.named(playerName, new PlayerdataPlayerFile(this,
				playerUUID));
	}

	public Player createPlayersPlayer(String playerName) {
		return Player
				.named(playerName, new PlayersPlayerFile(this, playerName));
	}

	/**
	 * Since version 1.7.6, minecraft stores players in the playerdata directory
	 * and uses the player uuid as filename.
	 */
	public List<Player> createMultiplayerPlayers() {
		List<Player> result = new ArrayList<Player>();
		for (File playerdataFile : getPlayerdataFiles()) {
			if (playerdataFile.isFile()) {
				String playerUUID = getPlayerUUIDFromPlayerdataFile(playerdataFile);
				result.add(createPlayerdataPlayer(
						getPlayerNameFromPlayerdataFile(playerUUID), playerUUID));
			}
		}
		if (!result.isEmpty()) {
			Log.i("using players from the playerdata directory");
			return result;
		}
		for (File playersFile : getPlayersFiles()) {
			if (playersFile.isFile()) {
				result.add(createPlayersPlayer(getPlayerNameFromPlayersFile(playersFile)));
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
	public List<Player> createSingleplayerPlayers() {
		Log.i("using player from level.dat");
		return Arrays.asList(createLevelDatPlayer());
	}

	private String getPlayerNameFromPlayerdataFile(String playerUUID) {
		return PlayerInformationRetriever.getPlayerName(playerUUID);
	}

	private String getPlayerUUIDFromPlayerdataFile(File playerdataFile) {
		return playerdataFile.getName().split("\\.")[0];
	}

	private String getPlayerNameFromPlayersFile(File playersFile) {
		return playersFile.getName().split("\\.")[0];
	}
}
