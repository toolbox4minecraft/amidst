package amidst.fragment.drawer;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.Fragment;
import amidst.fragment.layer.LayerDeclaration;
import amidst.gui.main.viewer.WorldIconSelection;
import amidst.gui.main.viewer.Zoom;
import amidst.mojangapi.world.coordinates.Coordinates;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.mojangapi.world.icon.WorldIcon;

@NotThreadSafe
public class WorldIconDrawer extends FragmentDrawer {
	private final Zoom zoom;
	private final WorldIconSelection worldIconSelection;

	public WorldIconDrawer(LayerDeclaration declaration, Zoom zoom, WorldIconSelection worldIconSelection) {
		super(declaration);
		this.zoom = zoom;
		this.worldIconSelection = worldIconSelection;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	public void draw(Fragment fragment, Graphics2D g2d, float time) {
		double invZoom = 1.0 / zoom.getCurrentValue();
		AffineTransform originalTransform = g2d.getTransform();
		for (WorldIcon icon : fragment.getWorldIcons(declaration.getLayerId())) {
			drawIcon(icon, invZoom, g2d);
			g2d.setTransform(originalTransform);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void drawIcon(WorldIcon icon, double invZoom, Graphics2D g2d) {
		BufferedImage image = icon.getImage().getImage();
		int width = image.getWidth();
		int height = image.getHeight();
		if (worldIconSelection.isSelected(icon)) {
			width *= 1.5;
			height *= 1.5;
		}
		Coordinates coordinates = icon.getCoordinates().getRelativeTo(Resolution.FRAGMENT);
		g2d.translate(coordinates.getX(), coordinates.getY());
		g2d.scale(invZoom, invZoom);
		g2d.drawImage(image, -(width >> 1), -(height >> 1), width, height, null);
	}
}
