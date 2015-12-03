package amidst.map.widget;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import amidst.fragment.layer.LayerReloader;
import amidst.map.BiomeSelection;
import amidst.map.Map;
import amidst.map.MapViewer;
import amidst.minecraft.world.World;
import amidst.resources.ResourceLoader;

public class BiomeToggleWidget extends Widget {
	private static final BufferedImage HIGHLIGHTER_ICON = ResourceLoader
			.getImage("highlighter.png");

	private final BiomeSelection biomeSelection;
	private final LayerReloader layerReloader;

	public BiomeToggleWidget(MapViewer mapViewer, Map map, World world,
			CornerAnchorPoint anchor, BiomeSelection biomeSelection,
			LayerReloader layerReloader) {
		super(mapViewer, map, world, anchor);
		this.biomeSelection = biomeSelection;
		this.layerReloader = layerReloader;
		setWidth(36);
		setHeight(36);
	}

	@Override
	public void draw(Graphics2D g2d, float time, FontMetrics fontMetrics) {
		drawBorderAndBackground(g2d, time);
		g2d.drawImage(HIGHLIGHTER_ICON, getX(), getY(), 36, 36, null);
	}

	@Override
	public boolean onMousePressed(int x, int y) {
		biomeSelection.toggleHighlightMode();
		layerReloader.reloadBiomeLayer();
		return true;
	}

	@Override
	protected boolean onVisibilityCheck() {
		return true;
	}
}
