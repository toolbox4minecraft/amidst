package amidst.gui.main.viewer;

import amidst.documentation.ThreadSafe;

@ThreadSafe
public class DimensionSelection {
	private volatile int dimensionId;

	public DimensionSelection(int dimensionId) {
		this.dimensionId = dimensionId;
	}

	public boolean isDimensionId(int dimensionId) {
		return this.dimensionId == dimensionId;
	}

	public void setDimensionId(int dimensionId) {
		this.dimensionId = dimensionId;
	}
}
