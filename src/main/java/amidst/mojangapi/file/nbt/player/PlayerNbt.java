package amidst.mojangapi.file.nbt.player;

import java.util.function.Function;
import java.util.function.Supplier;

import amidst.documentation.Immutable;
import amidst.mojangapi.world.player.PlayerCoordinates;

@Immutable
public abstract class PlayerNbt {
	private final PlayerCoordinates playerCoordinates;

	public PlayerNbt(PlayerCoordinates playerCoordinates) {
		this.playerCoordinates = playerCoordinates;
	}

	public PlayerCoordinates getPlayerCoordinates() {
		return playerCoordinates;
	}

	public abstract <R> R map(
			Supplier<R> ifIsLevelDat,
			Function<String, R> ifIsPlayerdata,
			Function<String, R> ifIsPlayers);
}
