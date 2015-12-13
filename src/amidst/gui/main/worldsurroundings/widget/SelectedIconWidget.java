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
	public void draw(Graphics2D g2d, FontMetrics fontMetrics, float time) {
		WorldIcon selection = worldIconSelection.get();
		if (selection != null) {
			message = selection.toString();
			icon = selection.getImage();
		}

		setWidth(45 + fontMetrics.stringWidth(message));
		drawBorderAndBackground(g2d, time);
		double imgWidth = icon.getWidth();
		double imgHeight = icon.getHeight();
		double ratio = imgWidth / imgHeight;

		g2d.drawImage(icon, getX() + 5, getY() + 5, (int) (25. * ratio), 25,
				null);
		g2d.drawString(message, getX() + 35, getY() + 23);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected boolean onVisibilityCheck() {
		return worldIconSelection.hasSelection();
	}
}
