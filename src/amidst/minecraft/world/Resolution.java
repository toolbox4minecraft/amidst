package amidst.minecraft.world;

public enum Resolution {
	WORLD(0), QUARTER(2), CHUNK(4), FRAGMENT(9);

	private final int shiftSize;

	private Resolution(int shiftSize) {
		this.shiftSize = shiftSize;
	}

	public int getShiftSize() {
		return shiftSize;
	}

	public int getSize() {
		return 1 << shiftSize;
	}

	public long convertFromWorldToThis(long coordinateInWorld) {
		return coordinateInWorld >> shiftSize;
	}

	public long convertFromThisToWorld(long coordinateInThisResolution) {
		return coordinateInThisResolution << shiftSize;
	}

	public long convertToThis(Resolution oldResolution,
			long coordinateInOldResolution) {
		return (coordinateInOldResolution << oldResolution.shiftSize) >> shiftSize;
	}
}
