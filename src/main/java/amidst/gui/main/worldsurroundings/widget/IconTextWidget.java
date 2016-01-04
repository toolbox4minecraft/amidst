package amidst.gui.main.worldsurroundings.widget;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;

@NotThreadSafe
public abstract class IconTextWidget extends Widget {
	private static final int ICON_HEIGHT = 25;

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
		updated = false;
		BufferedImage newIcon = updateIcon();
		if (newIcon != null && newIcon != icon) {
			icon = newIcon;
			iconWidth = (int) (((double) ICON_HEIGHT) * icon.getWidth() / icon
					.getHeight());
			updated = true;
		}
		String newText = updateText();
		if (newText != null && !newText.equals(text)) {
			text = newText;
			updated = true;
		}
		if (updated) {
			setWidth(getTextOffset() + fontMetrics.stringWidth(text) + 10);
		}
		isVisible = newIcon != null && newText != null;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected void doDraw(Graphics2D g2d) {
		g2d.drawImage(icon, getX() + 5, getY() + 5, iconWidth, ICON_HEIGHT,
				null);
		g2d.drawString(text, getX() + getTextOffset(), getY() + 23);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private int getTextOffset() {
		return 5 + iconWidth + 5;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected boolean onVisibilityCheck() {
		return isVisible;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	protected abstract BufferedImage updateIcon();

	@CalledOnlyBy(AmidstThread.EDT)
	protected abstract String updateText();
}
