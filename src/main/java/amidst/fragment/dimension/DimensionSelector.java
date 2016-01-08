package amidst.fragment.dimension;

import amidst.documentation.ThreadSafe;
import amidst.fragment.FragmentQueueProcessor;

@ThreadSafe
public class DimensionSelector {
	private final FragmentQueueProcessor fragmentQueueProcessor;

	public DimensionSelector(FragmentQueueProcessor fragmentQueueProcessor) {
		this.fragmentQueueProcessor = fragmentQueueProcessor;
	}

	public void selectDimension(int dimensionId) {
		fragmentQueueProcessor.selectDimension(dimensionId);
	}
}
