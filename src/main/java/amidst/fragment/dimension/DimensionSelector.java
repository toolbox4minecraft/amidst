package amidst.fragment.dimension;

import amidst.documentation.ThreadSafe;
import amidst.fragment.FragmentQueueProcessor;
import amidst.mojangapi.world.Dimension;

@ThreadSafe
public class DimensionSelector {
	private final FragmentQueueProcessor fragmentQueueProcessor;

	public DimensionSelector(FragmentQueueProcessor fragmentQueueProcessor) {
		this.fragmentQueueProcessor = fragmentQueueProcessor;
	}

	public void selectDimension(Dimension dimension) {
		fragmentQueueProcessor.selectDimension(dimension);
	}
}
