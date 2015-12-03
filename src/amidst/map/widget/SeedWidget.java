package amidst.map.widget;

import java.awt.FontMetrics;
import java.awt.Graphics2D;

import amidst.map.MapViewer;
import amidst.minecraft.world.World;

public class SeedWidget extends Widget {
	private final World world;

	public SeedWidget(MapViewer mapViewer, CornerAnchorPoint anchor, World world) {
		super(mapViewer, anchor);
		this.world = world;
		setWidth(20);
		setHeight(30);
	}

	@Override
	public void draw(Graphics2D g2d, float time, FontMetrics fontMetrics) {
		String text = getText();
		setWidth(fontMetrics.stringWidth(text) + 20);
		drawBorderAndBackground(g2d, time);
		drawText(g2d, text);
	}

	private void drawText(Graphics2D g2d, String text) {
		g2d.drawString(text, getX() + 10, getY() + 20);
	}

	public String getText() {
		String seedText = world.getSeedText();
		if (seedText == null) {
			return "Seed: " + world.getSeed();
		} else {
			return "Seed: " + seedText + " (" + world.getSeed() + ")";
		}
	}

	@Override
	protected boolean onVisibilityCheck() {
		return true;
	}
}
