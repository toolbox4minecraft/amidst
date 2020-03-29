package amidst.mojangapi.world.biome;

import amidst.documentation.Immutable;

@SuppressWarnings("serial")
@Immutable
public class UnknownBiomeNameException extends Exception {
	public UnknownBiomeNameException(String message) {
		super(message);
	}
}
