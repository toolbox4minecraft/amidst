package amidst.gui.main.viewer.widget;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;

public abstract class ImmutableIconWidget extends Widget {
	private BufferedImage icon;

	@CalledOnlyBy(AmidstThread.EDT)
	protected ImmutableIconWidget(CornerAnchorPoint anchor, BufferedImage icon) {
		super(anchor);
		this.icon = icon;
		setWidth(icon.getWidth());
		setHeight(icon.getHeight());
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected void doUpdate(FontMetrics fontMetrics, float time) {
		// noop
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected void doDraw(Graphics2D g2d) {
		g2d.drawImage(icon, getX(), getY(), icon.getWidth(), icon.getHeight(), null);
	}
}
