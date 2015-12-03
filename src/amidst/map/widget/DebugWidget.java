package amidst.map.widget;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import amidst.Options;
import amidst.map.FragmentGraph;
import amidst.map.FragmentManager;
import amidst.map.MapViewer;

public class DebugWidget extends Widget {
	private final FragmentGraph graph;
	private final FragmentManager fragmentManager;

	public DebugWidget(MapViewer mapViewer, CornerAnchorPoint anchor,
			FragmentGraph graph, FragmentManager fragmentManager) {
		super(mapViewer, anchor);
		this.graph = graph;
		this.fragmentManager = fragmentManager;
		forceVisibility(onVisibilityCheck());
	}

	@Override
	public void draw(Graphics2D g2d, float time, FontMetrics fontMetrics) {
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
		panelLines.add("Reset Queue Size: "
				+ fragmentManager.getResetQueueSize());
		panelLines.add("");
		panelLines.add("Map Viewer:");
		panelLines.add("Map Size: " + graph.getFragmentsPerRow() + "x"
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
		return Options.instance.showDebug.get();
	}
}
