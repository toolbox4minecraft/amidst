package amidst.map.widget;

import java.awt.Graphics2D;

import amidst.Options;
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
		String seedMessage = getSeedMessage();
		setWidth(mapViewer.getFontMetrics().stringWidth(seedMessage) + 20);
		super.draw(g2d, time);
		g2d.setColor(TEXT_COLOR);
		g2d.drawString(seedMessage, getX() + 10, getY() + 20);
	}

	public String getSeedMessage() {
		World world = Options.instance.world;
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
