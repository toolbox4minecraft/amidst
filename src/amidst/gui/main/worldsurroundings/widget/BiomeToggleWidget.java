package amidst.gui.main.worldsurroundings.widget;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.layer.LayerReloader;
import amidst.gui.main.worldsurroundings.BiomeSelection;
import amidst.resources.ResourceLoader;

@NotThreadSafe
public class BiomeToggleWidget extends Widget {
	private static final BufferedImage HIGHLIGHTER_ICON = ResourceLoader
			.getImage("highlighter.png");

	private final BiomeSelection biomeSelection;
	private final LayerReloader layerReloader;

	@CalledOnlyBy(AmidstThread.EDT)
	public BiomeToggleWidget(CornerAnchorPoint anchor,
			BiomeSelection biomeSelection, LayerReloader layerReloader) {
		super(anchor);
		this.biomeSelection = biomeSelection;
		this.layerReloader = layerReloader;
		setWidth(36);
		setHeight(36);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected void doUpdate(FontMetrics fontMetrics, float time) {
		// noop
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected void doDraw(Graphics2D g2d) {
		g2d.drawImage(HIGHLIGHTER_ICON, getX(), getY(), 36, 36, null);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	public boolean onMousePressed(int x, int y) {
		biomeSelection.toggleHighlightMode();
		layerReloader.reloadBiomeLayer();
		return true;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected boolean onVisibilityCheck() {
		return true;
	}
}
