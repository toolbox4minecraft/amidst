package amidst.mojangapi.world.coordinates;

import amidst.documentation.Immutable;

@Immutable
public enum Resolution {
	WORLD(0), QUARTER(2), CHUNK(4), FRAGMENT(9);

	private final int shift;

	private Resolution(int shift) {
		this.shift = shift;
	}

	public int getStep() {
		return 1 << shift;
	}

	public int getStepsPerFragment() {
		return 1 << (FRAGMENT.shift - shift);
	}

	public long convertFromWorldToThis(long coordinateInWorld) {
		return coordinateInWorld >> shift;
	}

	public long convertFromThisToWorld(long coordinateInThisResolution) {
		return coordinateInThisResolution << shift;
	}
}
