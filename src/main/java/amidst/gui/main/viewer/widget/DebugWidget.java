package amidst.gui.main.viewer.widget;

import java.util.Arrays;
import java.util.List;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.FragmentGraph;
import amidst.fragment.FragmentManager;
import amidst.settings.Setting;

@NotThreadSafe
public class DebugWidget extends MultilineTextWidget {
	private final FragmentGraph graph;
	private final FragmentManager fragmentManager;
	private final Setting<Boolean> isVisibleSetting;

	@CalledOnlyBy(AmidstThread.EDT)
	public DebugWidget(CornerAnchorPoint anchor, FragmentGraph graph,
			FragmentManager fragmentManager, Setting<Boolean> isVisibleSetting) {
		super(anchor);
		this.graph = graph;
		this.fragmentManager = fragmentManager;
		this.isVisibleSetting = isVisibleSetting;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected List<String> getTextLines() {
		if (isVisibleSetting.get()) {
			int columns = graph.getFragmentsPerRow();
			int rows = graph.getFragmentsPerColumn();
			// @formatter:off
			return Arrays.asList(
					"Fragment Manager:",
					"Cache Size: " + fragmentManager.getCacheSize(),
					"Available Queue Size: " + fragmentManager.getAvailableQueueSize(),
					"Loading Queue Size: " + fragmentManager.getLoadingQueueSize(),
					"Recycle Queue Size: " + fragmentManager.getRecycleQueueSize(),
					"",
					"Viewer:",
					"Size: " + columns + "x" + rows + " [" + (columns * rows) + "]",
					String.format("2D Accelerated: %1$.1f%%", 100.0f * this.getGraphicsAccelerationRatio())
			);
			// @formatter:on
		} else {
			return null;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	public boolean onMousePressed(int x, int y) {
		return false;
	}
}
