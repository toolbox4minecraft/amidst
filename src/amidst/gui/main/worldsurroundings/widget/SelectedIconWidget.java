package amidst.gui.main.worldsurroundings.widget;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.gui.main.worldsurroundings.WorldIconSelection;
import amidst.mojangapi.world.icon.WorldIcon;

@NotThreadSafe
public class SelectedIconWidget extends Widget {
	private final WorldIconSelection worldIconSelection;

	private String message = "";
	private BufferedImage icon;
	private int iconWidth;

	@CalledOnlyBy(AmidstThread.EDT)
	public SelectedIconWidget(CornerAnchorPoint anchor,
			WorldIconSelection worldIconSelection) {
		super(anchor);
		this.worldIconSelection = worldIconSelection;
		increaseYMargin(40);
		setWidth(20);
		setHeight(35);
		forceVisibility(false);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected void doUpdate(FontMetrics fontMetrics, float time) {
		WorldIcon selection = worldIconSelection.get();
		if (selection != null) {
			message = selection.toString();
			icon = selection.getImage();
			double ratio = (double) icon.getWidth() / (double) icon.getHeight();
			iconWidth = (int) (25. * ratio);
		}
		setWidth(45 + fontMetrics.stringWidth(message));
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected void doDraw(Graphics2D g2d) {
		g2d.drawImage(icon, getX() + 5, getY() + 5, iconWidth, 25, null);
		g2d.drawString(message, getX() + 35, getY() + 23);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected boolean onVisibilityCheck() {
		return worldIconSelection.hasSelection();
	}
}
