package amidst.mojangapi.world.biome;

import amidst.documentation.Immutable;

@Immutable
public class UnknownBiomeNameException extends Exception {
	private static final long serialVersionUID = 3034394814256519917L;

	public UnknownBiomeNameException(String message) {
		super(message);
	}
}
