package amidst.gui.main.worldsurroundings.widget;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.FragmentGraph;
import amidst.fragment.FragmentManager;
import amidst.settings.Setting;

@NotThreadSafe
public class DebugWidget extends Widget {
	private final FragmentGraph graph;
	private final FragmentManager fragmentManager;
	private final Setting<Boolean> isVisibleSetting;

	private List<String> panelLines;

	@CalledOnlyBy(AmidstThread.EDT)
	public DebugWidget(CornerAnchorPoint anchor, FragmentGraph graph,
			FragmentManager fragmentManager, Setting<Boolean> isVisibleSetting) {
		super(anchor);
		this.graph = graph;
		this.fragmentManager = fragmentManager;
		this.isVisibleSetting = isVisibleSetting;
		forceVisibility(onVisibilityCheck());
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected void doUpdate(FontMetrics fontMetrics, float time) {
		panelLines = getPanelLines();
		int width = getPanelWidth(fontMetrics);
		int height = getPanelHeight();
		setWidth(width);
		setHeight(height);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private List<String> getPanelLines() {
		List<String> panelLines = new ArrayList<String>();
		panelLines.add("Fragment Manager:");
		panelLines.add("Cache Size: " + fragmentManager.getCacheSize());
		panelLines.add("Available Queue Size: "
				+ fragmentManager.getAvailableQueueSize());
		panelLines.add("Loading Queue Size: "
				+ fragmentManager.getLoadingQueueSize());
		panelLines.add("Recycle Queue Size: "
				+ fragmentManager.getRecycleQueueSize());
		panelLines.add("");
		panelLines.add("Viewer:");
		panelLines.add("Size: " + graph.getFragmentsPerRow() + "x"
				+ graph.getFragmentsPerColumn() + " ["
				+ (graph.getFragmentsPerRow() * graph.getFragmentsPerColumn())
				+ "]");
		return panelLines;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private int getPanelWidth(FontMetrics fontMetrics) {
		int result = 0;
		for (String line : panelLines) {
			int textWidth = fontMetrics.stringWidth(line);
			if (result < textWidth) {
				result = textWidth;
			}
		}
		return result + 20;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private int getPanelHeight() {
		return panelLines.size() * 20 + 10;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected void doDraw(Graphics2D g2d) {
		drawPanelLines(g2d);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void drawPanelLines(Graphics2D g2d) {
		for (int i = 0; i < panelLines.size(); i++) {
			g2d.drawString(panelLines.get(i), getX() + 10, getY() + 20 + i * 20);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	public boolean onMousePressed(int x, int y) {
		return false;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected boolean onVisibilityCheck() {
		return isVisibleSetting.get();
	}
}
