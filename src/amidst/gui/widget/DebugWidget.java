package amidst.gui.widget;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import amidst.fragment.FragmentGraph;
import amidst.fragment.FragmentManager;
import amidst.preferences.PrefModel;

public class DebugWidget extends Widget {
	private final FragmentGraph graph;
	private final FragmentManager fragmentManager;
	private final PrefModel<Boolean> isVisiblePreference;

	public DebugWidget(CornerAnchorPoint anchor, FragmentGraph graph,
			FragmentManager fragmentManager,
			PrefModel<Boolean> isVisiblePreference) {
		super(anchor);
		this.graph = graph;
		this.fragmentManager = fragmentManager;
		this.isVisiblePreference = isVisiblePreference;
		forceVisibility(onVisibilityCheck());
	}

	@Override
	public void draw(Graphics2D g2d, int viewerWidth, int viewerHeight,
			Point mousePosition, FontMetrics fontMetrics, float time) {
		List<String> panelLines = getPanelLines();
		int width = getPanelWidth(panelLines, fontMetrics);
		int height = getPanelHeight(panelLines);
		setWidth(width);
		setHeight(height);
		drawBorderAndBackground(g2d, time, viewerWidth, viewerHeight);
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
		return isVisiblePreference.get();
	}
}
