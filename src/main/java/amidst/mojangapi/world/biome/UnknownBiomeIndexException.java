package amidst.mojangapi.world.biome;

import amidst.documentation.Immutable;

@SuppressWarnings("serial")
@Immutable
public class UnknownBiomeIndexException extends Exception {
	public UnknownBiomeIndexException(String message) {
		super(message);
	}
}
