package amidst.minecraft.world;

public enum Resolution {
	WORLD(0), QUARTER(2), CHUNK(4), FRAGMENT(9);

	private final int shift;

	private Resolution(int shift) {
		this.shift = shift;
	}

	public int getShift() {
		return shift;
	}

	public int getStep() {
		return 1 << shift;
	}

	public long convertFromWorldToThis(long coordinateInWorld) {
		return coordinateInWorld >> shift;
	}

	public long convertFromThisToWorld(long coordinateInThisResolution) {
		return coordinateInThisResolution << shift;
	}

	public long convertToThis(Resolution oldResolution,
			long coordinateInOldResolution) {
		return (coordinateInOldResolution << oldResolution.shift) >> shift;
	}
}
