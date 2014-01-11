package amidst.map.widget;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import amidst.map.layers.BiomeLayer;
import amidst.resources.ResourceLoader;
import MoF.MapViewer;

public class BiomeToggleWidget extends PanelWidget {
	private static BufferedImage highlighterIcon = ResourceLoader.getImage("highlighter.png");
	public static boolean isBiomeWidgetVisible = false;
	public BiomeToggleWidget(MapViewer mapViewer) {
		super(mapViewer);
		setDimensions(36, 36);
	}
	
	@Override
	public void draw(Graphics2D g2d, float time) {
		super.draw(g2d, time);
		g2d.drawImage(highlighterIcon, x, y, 36, 36, null);
	}

	@Override
	public boolean onMousePressed(int x, int y) {
		isBiomeWidgetVisible = !isBiomeWidgetVisible;
		BiomeLayer.instance.setHighlightMode(isBiomeWidgetVisible);
		(new Thread(new Runnable() {
			@Override
			public void run() {
				map.resetImageLayer(BiomeLayer.instance.getLayerId());
			}
		})).start();
		return true;
	}
}
