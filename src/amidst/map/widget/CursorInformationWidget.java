package amidst.map.widget;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;

import amidst.Options;
import amidst.map.Fragment;
import amidst.map.FragmentGraph;
import amidst.map.Map;
import amidst.map.MapViewer;
import amidst.minecraft.Biome;
import amidst.minecraft.world.CoordinatesInWorld;
import amidst.minecraft.world.Resolution;

public class CursorInformationWidget extends Widget {
	private static final String UNKNOWN_BIOME_NAME = "Unknown";

	private final FragmentGraph graph;
	private final Map map;
	private final Options options;

	private String text = "";

	public CursorInformationWidget(MapViewer mapViewer,
			CornerAnchorPoint anchor, FragmentGraph graph, Map map,
			Options options) {
		super(mapViewer, anchor);
		this.graph = graph;
		this.map = map;
		this.options = options;
		setWidth(20);
		setHeight(30);
		forceVisibility(false);
	}

	@Override
	public void draw(Graphics2D g2d, float time, FontMetrics fontMetrics) {
		String newText = getText();
		if (newText != null) {
			text = newText;
		}
		setWidth(g2d.getFontMetrics().stringWidth(text) + 20);
		drawBorderAndBackground(g2d, time);
		g2d.drawString(text, getX() + 10, getY() + 20);
	}

	private String getText() {
		Point mouse = mapViewer.getMousePosition();
		if (mouse != null) {
			CoordinatesInWorld coordinates = map.screenToWorld(mouse);
			String biomeName = getBiomeNameAt(coordinates);
			return biomeName + " " + coordinates.toString();
		} else {
			return null;
		}
	}

	private String getBiomeNameAt(CoordinatesInWorld coordinates) {
		Fragment fragment = graph.getFragmentAt(coordinates);
		if (fragment != null && fragment.isLoaded()) {
			long x = coordinates.getXRelativeToFragmentAs(Resolution.QUARTER);
			long y = coordinates.getYRelativeToFragmentAs(Resolution.QUARTER);
			short biome = fragment.getBiomeDataAt((int) x, (int) y);
			return Biome.getByIndex(biome).getName();
		} else {
			return UNKNOWN_BIOME_NAME;
		}
	}

	@Override
	protected boolean onVisibilityCheck() {
		return mapViewer.getMousePosition() != null;
	}
}
