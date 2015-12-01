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
	private final AffineTransform worldIconLayerMatrix = new AffineTransform();
	private final Map map;

	public WorldIconDrawer(LayerDeclaration declaration, Map map) {
		super(declaration);
		this.map = map;
	}

	@Override
	public void draw(Fragment fragment, Graphics2D g2d,
			AffineTransform layerMatrix) {
		double invZoom = 1.0 / map.getZoom();
		for (WorldIcon icon : fragment.getWorldIcons(declaration.getLayerId())) {
			drawIcon(icon, invZoom, g2d, layerMatrix);
		}
	}

	private void drawIcon(WorldIcon icon, double invZoom, Graphics2D g2d,
			AffineTransform layerMatrix) {
		BufferedImage image = icon.getImage();
		int width = image.getWidth();
		int height = image.getHeight();
		if (map.getSelectedWorldIcon() == icon) {
			width *= 1.5;
			height *= 1.5;
		}
		initIconLayerMatrix(invZoom, icon.getCoordinates(), layerMatrix);
		g2d.setTransform(worldIconLayerMatrix);
		g2d.drawImage(image, -(width >> 1), -(height >> 1), width, height, null);
	}

	private void initIconLayerMatrix(double invZoom,
			CoordinatesInWorld coordinates, AffineTransform layerMatrix) {
		worldIconLayerMatrix.setTransform(layerMatrix);
		worldIconLayerMatrix.translate(coordinates.getXRelativeToFragment(),
				coordinates.getYRelativeToFragment());
		worldIconLayerMatrix.scale(invZoom, invZoom);
	}
}
