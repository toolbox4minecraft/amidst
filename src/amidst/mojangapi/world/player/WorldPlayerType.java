package amidst.mojangapi.world.player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import amidst.mojangapi.file.directory.SaveDirectory;
import amidst.mojangapi.file.nbt.LevelDat;

public enum WorldPlayerType {
	/**
	 * Only the selectable options need a name.
	 */
	// @formatter:off
	NONE(null),
	SINGLEPLAYER("Singleplayer"),
	MULTIPLAYER("Multiplayer"),
	BOTH("Both");
	// @formatter:on

	private static final List<WorldPlayerType> SELECTABLE = Arrays.asList(
			MULTIPLAYER, SINGLEPLAYER, BOTH);

	public static List<WorldPlayerType> getSelectable() {
		return SELECTABLE;
	}

	public static WorldPlayerType from(SaveDirectory saveDirectory,
			LevelDat levelDat) {
		if (saveDirectory.hasMultiplayerPlayers()) {
			if (levelDat.hasPlayer()) {
				return BOTH;
			} else {
				return MULTIPLAYER;
			}
		} else {
			return SINGLEPLAYER;
		}
	}

	private final String name;

	private WorldPlayerType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public List<Player> createPlayers(SaveDirectory saveDirectory) {
		if (this == NONE) {
			return Collections.emptyList();
		} else if (this == SINGLEPLAYER) {
			return saveDirectory.createSingleplayerPlayers();
		} else if (this == MULTIPLAYER) {
			return saveDirectory.createMultiplayerPlayers();
		} else {
			List<Player> result = saveDirectory.createMultiplayerPlayers();
			result.addAll(saveDirectory.createSingleplayerPlayers());
			return result;
		}
	}

	@Override
	public String toString() {
		return name;
	}
}
