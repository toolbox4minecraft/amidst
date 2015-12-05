package amidst.gui.widget;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import amidst.gui.worldsurroundings.WorldIconSelection;
import amidst.minecraft.world.icon.WorldIcon;

public class SelectedIconWidget extends Widget {
	private final WorldIconSelection worldIconSelection;

	private String message = "";
	private BufferedImage icon;

	public SelectedIconWidget(CornerAnchorPoint anchor,
			WorldIconSelection worldIconSelection) {
		super(anchor);
		this.worldIconSelection = worldIconSelection;
		increaseYMargin(40);
		setWidth(20);
		setHeight(35);
		forceVisibility(false);
	}

	@Override
	public void draw(Graphics2D g2d, float time, FontMetrics fontMetrics,
			int viewerWidth, int viewerHeight, Point mousePosition) {
		if (worldIconSelection.hasSelection()) {
			WorldIcon selection = worldIconSelection.get();
			message = selection.toString();
			icon = selection.getImage();
		}

		setWidth(45 + fontMetrics.stringWidth(message));
		drawBorderAndBackground(g2d, time, viewerWidth, viewerHeight);
		double imgWidth = icon.getWidth();
		double imgHeight = icon.getHeight();
		double ratio = imgWidth / imgHeight;

		g2d.drawImage(icon, getX() + 5, getY() + 5, (int) (25. * ratio), 25,
				null);
		g2d.drawString(message, getX() + 35, getY() + 23);
	}

	@Override
	protected boolean onVisibilityCheck() {
		return worldIconSelection.hasSelection();
	}
}
