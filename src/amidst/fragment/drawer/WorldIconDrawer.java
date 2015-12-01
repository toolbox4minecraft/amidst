package amidst.fragment.drawer;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import amidst.fragment.layer.LayerDeclaration;
import amidst.map.Fragment;
import amidst.map.Map;
import amidst.minecraft.world.CoordinatesInWorld;
import amidst.minecraft.world.object.WorldIcon;

public class WorldIconDrawer extends FragmentDrawer {
	private final AffineTransform worldObjectLayerMatrix = new AffineTransform();
	private final Map map;

	public WorldIconDrawer(LayerDeclaration declaration, Map map) {
		super(declaration);
		this.map = map;
	}

	@Override
	public void draw(Fragment fragment, Graphics2D g2d,
			AffineTransform layerMatrix) {
		double invZoom = 1.0 / map.getZoom();
		for (WorldIcon worldObject : fragment.getWorldObjects(declaration
				.getLayerId())) {
			drawObject(worldObject, invZoom, g2d, layerMatrix);
		}
	}

	private void drawObject(WorldIcon worldObject, double invZoom,
			Graphics2D g2d, AffineTransform layerMatrix) {
		BufferedImage image = worldObject.getImage();
		int width = image.getWidth();
		int height = image.getHeight();
		if (map.getSelectedWorldObject() == worldObject) {
			width *= 1.5;
			height *= 1.5;
		}
		initIconLayerMatrix(invZoom, worldObject.getCoordinates(), layerMatrix);
		g2d.setTransform(worldObjectLayerMatrix);
		g2d.drawImage(image, -(width >> 1), -(height >> 1), width, height, null);
	}

	private void initIconLayerMatrix(double invZoom,
			CoordinatesInWorld coordinates, AffineTransform layerMatrix) {
		worldObjectLayerMatrix.setTransform(layerMatrix);
		worldObjectLayerMatrix.translate(coordinates.getXRelativeToFragment(),
				coordinates.getYRelativeToFragment());
		worldObjectLayerMatrix.scale(invZoom, invZoom);
	}
}
