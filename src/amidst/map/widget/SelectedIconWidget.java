package amidst.map.widget;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import amidst.map.Map;
import amidst.map.MapViewer;
import amidst.map.WorldIconSelection;
import amidst.minecraft.world.World;
import amidst.minecraft.world.icon.WorldIcon;

public class SelectedIconWidget extends Widget {
	private final WorldIconSelection worldIconSelection;

	private String message = "";
	private BufferedImage icon;

	public SelectedIconWidget(MapViewer mapViewer, Map map, World world,
			CornerAnchorPoint anchor, WorldIconSelection worldIconSelection) {
		super(mapViewer, map, world, anchor);
		this.worldIconSelection = worldIconSelection;
		increaseYMargin(40);
		setWidth(20);
		setHeight(35);
		forceVisibility(false);
	}

	@Override
	public void draw(Graphics2D g2d, float time, FontMetrics fontMetrics) {
		if (worldIconSelection.hasSelection()) {
			WorldIcon selection = worldIconSelection.get();
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

	@Override
	protected boolean onVisibilityCheck() {
		return worldIconSelection.hasSelection();
	}
}
