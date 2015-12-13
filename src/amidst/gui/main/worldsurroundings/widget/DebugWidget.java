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
	public void draw(Graphics2D g2d, FontMetrics fontMetrics, float time) {
		List<String> panelLines = getPanelLines();
		int width = getPanelWidth(panelLines, fontMetrics);
		int height = getPanelHeight(panelLines);
		setWidth(width);
		setHeight(height);
		drawBorderAndBackground(g2d, time);
		drawPanelLines(g2d, panelLines);
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
	private int getPanelWidth(List<String> panelLines, FontMetrics fontMetrics) {
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
	private int getPanelHeight(List<String> panelLines) {
		return panelLines.size() * 20 + 10;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void drawPanelLines(Graphics2D g2d, List<String> panelLines) {
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
