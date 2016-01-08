package amidst.gui.main.viewer;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.Dimension;

@ThreadSafe
public class DimensionSelection {
	private volatile Dimension dimension;

	public DimensionSelection(Dimension dimension) {
		this.dimension = dimension;
	}

	public boolean isDimension(Dimension dimension) {
		return this.dimension == dimension;
	}

	public void setDimension(Dimension dimension) {
		this.dimension = dimension;
	}
}
