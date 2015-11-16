package amidst.map.widget;

import java.awt.Graphics2D;

import amidst.Options;
import amidst.map.MapViewer;
import amidst.minecraft.world.World;

public class SeedWidget extends PanelWidget {
	public SeedWidget(MapViewer mapViewer) {
		super(mapViewer);
		setSize(20, 30);
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
}
