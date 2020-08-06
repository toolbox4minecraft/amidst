package amidst.gui.main.viewer.widget;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.world.icon.WorldIconImage;

@NotThreadSafe
public abstract class IconTextWidget extends TextWidget {
	private static final int ICON_HEIGHT = 24;

	private WorldIconImage icon = null;
	private int iconWidth;
	private int iconHeight;
	private int iconOffsetY;

	@CalledOnlyBy(AmidstThread.EDT)
	protected IconTextWidget(CornerAnchorPoint anchor) {
		super(anchor);
		setWidth(20);
		setHeight(35);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected void doUpdate(FontMetrics fontMetrics, float time) {
		WorldIconImage newIcon = updateIcon();
		if (newIcon != null && newIcon != icon) {
			icon = newIcon;
			double scale = ((double) ICON_HEIGHT) / icon.getFrameHeight();
			iconWidth = (int) Math.round(scale * icon.getImage().getWidth());
			iconHeight = (int) Math.round(scale * icon.getImage().getHeight());
			iconOffsetY = (int) Math.round(scale * icon.getFrameOffsetY());
		}
		super.doUpdate(fontMetrics, time);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected void doDraw(Graphics2D g2d) {
		super.doDraw(g2d);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.drawImage(icon.getImage(), getX() + 5, getY() + 5 - iconOffsetY, iconWidth, iconHeight, null);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
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
	protected abstract WorldIconImage updateIcon();
}
