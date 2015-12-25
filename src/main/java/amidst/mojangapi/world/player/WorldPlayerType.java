package amidst.mojangapi.world.player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.mojangapi.file.directory.SaveDirectory;
import amidst.mojangapi.file.nbt.LevelDatNbt;
import amidst.mojangapi.file.nbt.player.PlayerNbt;

@Immutable
public enum WorldPlayerType {
	// Only the selectable options need a name.
	// @formatter:off
	NONE(null),
	SINGLEPLAYER("Singleplayer"),
	MULTIPLAYER("Multiplayer"),
	BOTH("Both");
	// @formatter:on

	private static final List<WorldPlayerType> SELECTABLE = Arrays.asList(
			SINGLEPLAYER, MULTIPLAYER, BOTH);

	public static List<WorldPlayerType> getSelectable() {
		return SELECTABLE;
	}

	public static WorldPlayerType from(SaveDirectory saveDirectory,
			LevelDatNbt levelDat) {
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

	@NotNull
	public List<PlayerNbt> createPlayerNbts(SaveDirectory saveDirectory) {
		if (this == NONE) {
			return Collections.emptyList();
		} else if (this == SINGLEPLAYER) {
			return saveDirectory.createSingleplayerPlayerNbts();
		} else if (this == MULTIPLAYER) {
			return saveDirectory.createMultiplayerPlayerNbts();
		} else {
			List<PlayerNbt> result = saveDirectory
					.createMultiplayerPlayerNbts();
			result.addAll(saveDirectory.createSingleplayerPlayerNbts());
			return result;
		}
	}

	@Override
	public String toString() {
		return name;
	}
}
