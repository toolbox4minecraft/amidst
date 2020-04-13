package amidst.mojangapi.world.biome;

import amidst.documentation.Immutable;

@Immutable
public class UnknownBiomeIdException extends Exception {
	private static final long serialVersionUID = -1477769755204737332L;

	public UnknownBiomeIdException(String message) {
		super(message);
	}
}
