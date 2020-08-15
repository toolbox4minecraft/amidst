package amidst.gui.main.viewer.widget;

import java.util.Arrays;
import java.util.List;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.FragmentGraph;
import amidst.fragment.FragmentManager;
import amidst.gui.main.viewer.Graphics2DAccelerationCounter;
import amidst.gui.main.viewer.Zoom;
import amidst.settings.Setting;

@NotThreadSafe
public class DebugWidget extends TextWidget {
	private final FragmentGraph graph;
	private final FragmentManager fragmentManager;
	private final Setting<Boolean> isVisibleSetting;
	private final Graphics2DAccelerationCounter accelerationCounter;
	private final Zoom zoom;

	@CalledOnlyBy(AmidstThread.EDT)
	public DebugWidget(
			CornerAnchorPoint anchor,
			FragmentGraph graph,
			FragmentManager fragmentManager,
			Setting<Boolean> isVisibleSetting,
			Graphics2DAccelerationCounter accelerationCounter,
			Zoom zoom) {
		super(anchor);
		this.graph = graph;
		this.fragmentManager = fragmentManager;
		this.isVisibleSetting = isVisibleSetting;
		this.accelerationCounter = accelerationCounter;
		this.zoom = zoom;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected List<String> updateTextLines() {
		if (isVisibleSetting.get()) {
			int columns = graph.getFragmentsPerRow();
			int rows = graph.getFragmentsPerColumn();
			return Arrays.asList(
					"Fragment Manager:",
					"Cache Size: " + fragmentManager.getCacheSize(),
					"Available Queue Size: " + fragmentManager.getAvailableQueueSize(),
					"Loading Queue Size: " + fragmentManager.getLoadingQueueSize(),
					"Recycle Queue Size: " + fragmentManager.getRecycleQueueSize(),
					"",
					"Viewer:",
					"Size: " + columns + "x" + rows + " [" + (columns * rows) + "]",
					String.format("Pixel Scale: %1$.3fx", zoom.getCurrentValue() * 4),
					String.format("Acceleration: %1$.1f%%", accelerationCounter.getAcceleratedPercentage()));
		} else {
			return null;
		}
	}
}
