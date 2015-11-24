package amidst.map.widget;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import amidst.Options;
import amidst.map.FragmentManager;
import amidst.map.Map;
import amidst.map.MapViewer;
import amidst.minecraft.world.World;

public class DebugWidget extends Widget {
	public DebugWidget(MapViewer mapViewer, Map map, World world,
			CornerAnchorPoint anchor) {
		super(mapViewer, map, world, anchor);
		forceVisibility(onVisibilityCheck());
	}

	@Override
	public void draw(Graphics2D g2d, float time, FontMetrics fontMetrics) {
		List<String> panelLines = getPanelLines(map.getFragmentManager());
		int width = getPanelWidth(panelLines, fontMetrics);
		int height = getPanelHeight(panelLines);
		setWidth(width);
		setHeight(height);
		drawBorderAndBackground(g2d, time);
		drawPanelLines(g2d, panelLines);
	}

	private List<String> getPanelLines(FragmentManager fragmentManager) {
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
		panelLines.add("Map Size: " + map.getFragmentsPerRow() + "x"
				+ map.getFragmentsPerColumn() + " ["
				+ (map.getFragmentsPerRow() * map.getFragmentsPerColumn())
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
