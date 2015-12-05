package amidst.gui.widget;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;

import amidst.fragment.Fragment;
import amidst.fragment.FragmentGraph;
import amidst.gui.worldsurroundings.Map;
import amidst.minecraft.Biome;
import amidst.minecraft.world.CoordinatesInWorld;
import amidst.minecraft.world.Resolution;

public class CursorInformationWidget extends Widget {
	private static final String UNKNOWN_BIOME_NAME = "Unknown";

	private final FragmentGraph graph;
	private final Map map;

	private Point mousePosition;
	private String text = "";

	public CursorInformationWidget(CornerAnchorPoint anchor,
			FragmentGraph graph, Map map) {
		super(anchor);
		this.graph = graph;
		this.map = map;
		setWidth(20);
		setHeight(30);
		forceVisibility(false);
	}

	@Override
	public void draw(Graphics2D g2d, float time, FontMetrics fontMetrics,
			int viewerWidth, int viewerHeight, Point mousePosition) {
		this.mousePosition = mousePosition;
		String newText = getText();
		if (newText != null) {
			text = newText;
		}
		setWidth(g2d.getFontMetrics().stringWidth(text) + 20);
		drawBorderAndBackground(g2d, time, viewerWidth, viewerHeight);
		g2d.drawString(text, getX() + 10, getY() + 20);
	}

	private String getText() {
		if (mousePosition != null) {
			CoordinatesInWorld coordinates = map.screenToWorld(mousePosition);
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
		return mousePosition != null;
	}
}
