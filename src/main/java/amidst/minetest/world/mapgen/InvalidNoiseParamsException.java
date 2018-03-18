package amidst.minetest.world.mapgen;

import amidst.documentation.Immutable;

@SuppressWarnings("serial")
@Immutable
public class InvalidNoiseParamsException extends Exception {

	public InvalidNoiseParamsException() {
		super();
	}
	
	public InvalidNoiseParamsException(String message) {
		super(message);
	}
}
