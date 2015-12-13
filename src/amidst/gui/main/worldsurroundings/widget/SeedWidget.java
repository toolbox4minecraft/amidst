package amidst.gui.main.worldsurroundings.widget;

import java.awt.FontMetrics;
import java.awt.Graphics2D;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.world.WorldSeed;

@NotThreadSafe
public class SeedWidget extends Widget {
	private final String text;

	@CalledOnlyBy(AmidstThread.EDT)
	public SeedWidget(CornerAnchorPoint anchor, WorldSeed seed) {
		super(anchor);
		this.text = seed.getLabel();
		setWidth(20);
		setHeight(30);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	public void draw(Graphics2D g2d, FontMetrics fontMetrics, float time) {
		setWidth(fontMetrics.stringWidth(text) + 20);
		drawBorderAndBackground(g2d, time);
		drawText(g2d, text);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void drawText(Graphics2D g2d, String text) {
		g2d.drawString(text, getX() + 10, getY() + 20);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected boolean onVisibilityCheck() {
		return true;
	}
}
