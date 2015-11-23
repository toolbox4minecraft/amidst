package amidst.minecraft.world;

public enum CoordinatesResolution {
	WORLD(0), CHUNK(4), QUARTER(2), FRAGMENT(9);

	private final int shiftSize;

	private CoordinatesResolution(int shiftSize) {
		this.shiftSize = shiftSize;
	}

	public int getShiftSize() {
		return shiftSize;
	}

	public int getSize() {
		return 1 << shiftSize;
	}

	public long shift(long coordinateInWorld) {
		return coordinateInWorld >> shiftSize;
	}
}
