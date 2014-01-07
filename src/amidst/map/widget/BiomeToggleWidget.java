package amidst.map.widget;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import amidst.resources.ResourceLoader;
import MoF.MapViewer;

public class BiomeToggleWidget extends PanelWidget {
	public static BiomeToggleWidget instance;
	private static BufferedImage highlighterIcon = ResourceLoader.getImage("highlighter.png");
	public boolean isBiomeWidgetVisible = false;
	public BiomeToggleWidget(MapViewer mapViewer) {
		super(mapViewer);
		setDimensions(36, 36);
		instance = this;
	}
	
	@Override
	public void draw(Graphics2D g2d, float time) {
		super.draw(g2d, time);
		g2d.drawImage(highlighterIcon, x, y, 36, 36, null);
	}

	@Override
	public void onClick(int x, int y) {
		isBiomeWidgetVisible = !isBiomeWidgetVisible;
	}
}
