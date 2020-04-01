package amidst.mojangapi.world.biome;

import amidst.documentation.Immutable;

@SuppressWarnings("serial")
@Immutable
public class UnknownBiomeIdException extends Exception {
	public UnknownBiomeIdException(String message) {
		super(message);
	}
}
