package amidst.mojangapi.world.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.mojangapi.file.directory.SaveDirectory;
import amidst.mojangapi.file.nbt.LevelDatNbt;
import amidst.mojangapi.file.nbt.player.PlayerNbt;
import amidst.mojangapi.file.service.SaveDirectoryService;

@Immutable
public enum WorldPlayerType {
	// @formatter:off
	NONE        ("None"),
	SINGLEPLAYER("Singleplayer"),
	MULTIPLAYER ("Multiplayer"),
	BOTH        ("Both");
	// @formatter:on

	private static final List<WorldPlayerType> SELECTABLE = Arrays.asList(SINGLEPLAYER, MULTIPLAYER, BOTH);

	public static List<WorldPlayerType> getSelectable() {
		return SELECTABLE;
	}

	public static WorldPlayerType from(SaveDirectory saveDirectory, LevelDatNbt levelDat) {
		boolean hasSingleplayerPlayer = levelDat.hasPlayer();
		boolean hasMultiplayerPlayers = saveDirectory.hasMultiplayerPlayers();
		if (hasSingleplayerPlayer && hasMultiplayerPlayers) {
			return BOTH;
		} else if (hasSingleplayerPlayer) {
			return SINGLEPLAYER;
		} else if (hasMultiplayerPlayers) {
			return MULTIPLAYER;
		} else {
			return NONE;
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
		SaveDirectoryService saveDirectoryService = new SaveDirectoryService();
		if (this == BOTH) {
			List<PlayerNbt> result = new ArrayList<>();
			result.addAll(saveDirectoryService.createSingleplayerPlayerNbts(saveDirectory));
			result.addAll(saveDirectoryService.createMultiplayerPlayerNbts(saveDirectory));
			return result;
		} else if (this == SINGLEPLAYER) {
			return saveDirectoryService.createSingleplayerPlayerNbts(saveDirectory);
		} else if (this == MULTIPLAYER) {
			return saveDirectoryService.createMultiplayerPlayerNbts(saveDirectory);
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public String toString() {
		return name;
	}
}
