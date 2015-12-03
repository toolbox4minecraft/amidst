package amidst.fragment.drawer;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import amidst.fragment.layer.LayerDeclaration;
import amidst.map.Fragment;
import amidst.map.Map;
import amidst.minecraft.world.CoordinatesInWorld;
import amidst.minecraft.world.icon.WorldIcon;

public class WorldIconDrawer extends FragmentDrawer {
	private final Map map;

	public WorldIconDrawer(LayerDeclaration declaration, Map map) {
		super(declaration);
		this.map = map;
	}

	@Override
	public void draw(Fragment fragment, Graphics2D g2d) {
		double invZoom = 1.0 / map.getZoom();
		AffineTransform originalTransform = g2d.getTransform();
		for (WorldIcon icon : fragment.getWorldIcons(declaration.getLayerId())) {
			drawIcon(icon, invZoom, g2d);
			g2d.setTransform(originalTransform);
		}
	}

	private void drawIcon(WorldIcon icon, double invZoom, Graphics2D g2d) {
		BufferedImage image = icon.getImage();
		int width = image.getWidth();
		int height = image.getHeight();
		if (map.getWorldIconSelection().isSelected(icon)) {
			width *= 1.5;
			height *= 1.5;
		}
		CoordinatesInWorld coordinates = icon.getCoordinates();
		g2d.translate(coordinates.getXRelativeToFragment(),
				coordinates.getYRelativeToFragment());
		g2d.scale(invZoom, invZoom);
		g2d.drawImage(image, -(width >> 1), -(height >> 1), width, height, null);
	}
}
