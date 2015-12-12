package amidst.gui.widget;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import amidst.fragment.FragmentGraph;
import amidst.fragment.FragmentManager;
import amidst.settings.Setting;

public class DebugWidget extends Widget {
	private final FragmentGraph graph;
	private final FragmentManager fragmentManager;
	private final Setting<Boolean> isVisiblePreference;

	public DebugWidget(CornerAnchorPoint anchor, FragmentGraph graph,
			FragmentManager fragmentManager,
			Setting<Boolean> isVisiblePreference) {
		super(anchor);
		this.graph = graph;
		this.fragmentManager = fragmentManager;
		this.isVisiblePreference = isVisiblePreference;
		forceVisibility(onVisibilityCheck());
	}

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

	private int getPanelHeight(List<String> panelLines) {
		return panelLines.size() * 20 + 10;
	}

	private void drawPanelLines(Graphics2D g2d, List<String> panelLines) {
		for (int i = 0; i < panelLines.size(); i++) {
			g2d.drawString(panelLines.get(i), getX() + 10, getY() + 20 + i * 20);
		}
	}

	@Override
	public boolean onMousePressed(int x, int y) {
		return false;
	}

	@Override
	protected boolean onVisibilityCheck() {
		return isVisiblePreference.get();
	}
}
