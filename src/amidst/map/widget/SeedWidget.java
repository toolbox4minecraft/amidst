package amidst.map.widget;

import java.awt.Graphics2D;

import amidst.map.Map;
import amidst.map.MapViewer;
import amidst.minecraft.world.World;

public class SeedWidget extends Widget {
	public SeedWidget(MapViewer mapViewer, Map map, World world,
			CornerAnchorPoint anchor) {
		super(mapViewer, map, world, anchor);
		setWidth(20);
		setHeight(30);
	}

	@Override
	public void draw(Graphics2D g2d, float time) {
		String text = getText();
		setWidth(mapViewer.getFontMetrics().stringWidth(text) + 20);
		super.draw(g2d, time);
		drawText(g2d, text);
	}

	private void drawText(Graphics2D g2d, String text) {
		g2d.setColor(TEXT_COLOR);
		g2d.drawString(text, getX() + 10, getY() + 20);
	}

	public String getText() {
		String seedText = world.getSeedText();
		if (seedText == null) {
			return "Seed: " + world.getSeed();
		} else {
			return "Seed: \"" + seedText + "\" (" + world.getSeed() + ")";
		}
	}

	@Override
	protected boolean onVisibilityCheck() {
		return true;
	}
}
