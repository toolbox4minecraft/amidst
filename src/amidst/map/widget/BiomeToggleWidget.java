package amidst.map.widget;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import amidst.map.MapViewer;
import amidst.map.layer.BiomeLayer;
import amidst.resources.ResourceLoader;

public class BiomeToggleWidget extends Widget {
	private static final BufferedImage HIGHLIGHTER_ICON = ResourceLoader
			.getImage("highlighter.png");
	public static boolean isBiomeWidgetVisible = false;

	public BiomeToggleWidget(MapViewer mapViewer, CornerAnchorPoint anchor) {
		super(mapViewer, anchor);
		setWidth(36);
		setHeight(36);
	}

	@Override
	public void draw(Graphics2D g2d, float time) {
		super.draw(g2d, time);
		g2d.drawImage(HIGHLIGHTER_ICON, getX(), getY(), 36, 36, null);
	}

	@Override
	public boolean onMousePressed(int x, int y) {
		isBiomeWidgetVisible = !isBiomeWidgetVisible;
		BiomeLayer.getInstance().setHighlightMode(isBiomeWidgetVisible);
		new Thread(new Runnable() {
			@Override
			public void run() {
				map.repaintImageLayer(BiomeLayer.getInstance().getLayerId());
			}
		}).start();
		return true;
	}

	@Override
	protected boolean onVisibilityCheck() {
		return true;
	}
}
