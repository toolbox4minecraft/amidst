package amidst.fragment.drawer;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import amidst.fragment.layer.LayerDeclaration;
import amidst.map.Fragment;
import amidst.map.Map;
import amidst.map.WorldIconSelection;
import amidst.minecraft.world.CoordinatesInWorld;
import amidst.minecraft.world.icon.WorldIcon;

public class WorldIconDrawer extends FragmentDrawer {
	private final Map map;
	private final WorldIconSelection worldIconSelection;

	public WorldIconDrawer(LayerDeclaration declaration, Map map,
			WorldIconSelection worldIconSelection) {
		super(declaration);
		this.map = map;
		this.worldIconSelection = worldIconSelection;
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
		if (worldIconSelection.isSelected(icon)) {
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
