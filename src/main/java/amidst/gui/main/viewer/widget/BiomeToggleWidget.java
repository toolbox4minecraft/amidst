package amidst.gui.main.viewer.widget;

import java.awt.Color;
import java.awt.Graphics2D;

import amidst.ResourceLoader;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.gui.main.viewer.BiomeSelection;

@NotThreadSafe
public class BiomeToggleWidget extends ImmutableIconWidget {
	private final BiomeWidget biomeWidget;
	private final BiomeSelection biomeSelection;

	@CalledOnlyBy(AmidstThread.EDT)
	public BiomeToggleWidget(CornerAnchorPoint anchor, BiomeWidget biomeWidget, BiomeSelection biomeSelection) {
		super(anchor, ResourceLoader.getImage("/amidst/gui/main/highlighter.png"));
		this.biomeWidget = biomeWidget;
		this.biomeSelection = biomeSelection;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	public boolean onMousePressed(int x, int y) {
		biomeWidget.toggleVisibility();
		return true;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected void doDraw(Graphics2D g2d) {
		super.doDraw(g2d);
		if(biomeSelection.isHighlightMode()) {
			g2d.setColor(new Color(255, 255, 255, 64));
			g2d.fillRect(getX(), getY(), getWidth(), getHeight());
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected boolean onVisibilityCheck() {
		return true;
	}
}
