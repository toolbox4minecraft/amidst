package amidst.map.widget;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;

import amidst.map.Map;
import amidst.map.MapViewer;
import amidst.minecraft.world.World;

public class CursorInformationWidget extends Widget {
	private String text = "";

	public CursorInformationWidget(MapViewer mapViewer, Map map, World world,
			CornerAnchorPoint anchor) {
		super(mapViewer, map, world, anchor);
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
			Point pointInWorld = map.screenToWorld(mouse);
			return map.getBiomeAliasAt(pointInWorld) + " [ " + pointInWorld.x
					+ ", " + pointInWorld.y + " ]";
		} else {
			return null;
		}
	}

	@Override
	protected boolean onVisibilityCheck() {
		return mapViewer.getMousePosition() != null;
	}
}
