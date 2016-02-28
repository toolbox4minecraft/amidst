package amidst.gui.main.viewer.widget;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;

@NotThreadSafe
public abstract class IconTextWidget extends TextWidget {
	private static final int ICON_HEIGHT = 24;

	private boolean updated;
	private BufferedImage icon = null;
	private int iconWidth;
	private String text = "";
	private boolean isVisible = false;

	@CalledOnlyBy(AmidstThread.EDT)
	protected IconTextWidget(CornerAnchorPoint anchor) {
		super(anchor);
		setWidth(20);
		setHeight(35);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected void doUpdate(FontMetrics fontMetrics, float time) {
		BufferedImage newIcon = updateIcon();
		if (newIcon != null && newIcon != icon) {
			icon = newIcon;
			iconWidth = (int) (((double) ICON_HEIGHT) * icon.getWidth() / icon
					.getHeight());
			updated = true;
		}
		super.doUpdate(fontMetrics, time);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected void doDraw(Graphics2D g2d) {
		super.doDraw(g2d);
		g2d.drawImage(icon, getX() + 5, getY() + 5, iconWidth, ICON_HEIGHT,
				null);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected int getMarginLeft() {
		return 5 + iconWidth + 5;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected int getMarginTop() {
		// Lower the text slightly to align it with the icon
		return super.getMarginTop() + 2;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected int getMinimumHeight() {
		return 5 + ICON_HEIGHT + 5;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	protected abstract BufferedImage updateIcon();

}
