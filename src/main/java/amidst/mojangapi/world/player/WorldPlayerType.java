package amidst.mojangapi.world.player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.mojangapi.file.SaveGame;
import amidst.mojangapi.file.SaveGamePlayer;

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

	public static WorldPlayerType from(SaveGame saveGame) {
		boolean hasSingleplayerPlayer = saveGame.hasSingleplayerPlayer();
		boolean hasMultiplayerPlayers = saveGame.hasMultiplayerPlayers();
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
	public List<SaveGamePlayer> tryReadPlayers(SaveGame saveGame) {
		if (this == BOTH) {
			return saveGame.tryReadAllPlayers();
		} else if (this == SINGLEPLAYER) {
			return saveGame
					.tryReadSingleplayerPlayer()
					.map(Collections::singletonList)
					.orElseGet(Collections::emptyList);
		} else if (this == MULTIPLAYER) {
			return saveGame.tryReadMultiplayerPlayers();
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public String toString() {
		return name;
	}
}
