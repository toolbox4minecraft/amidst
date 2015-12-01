package amidst.fragment.drawer;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

import amidst.fragment.layer.LayerDeclaration;
import amidst.map.Fragment;
import amidst.minecraft.world.Resolution;

public class ImageDrawer extends FragmentDrawer {
	private final AffineTransform imageLayerMatrix = new AffineTransform();
	private final Resolution resolution;

	public ImageDrawer(LayerDeclaration declaration, Resolution resolution) {
		super(declaration);
		this.resolution = resolution;
	}

	@Override
	public void draw(Fragment fragment, Graphics2D g2d,
			AffineTransform layerMatrix) {
		initImageLayerMatrix(resolution.getStep(), layerMatrix);
		g2d.setTransform(imageLayerMatrix);
		if (g2d.getTransform().getScaleX() < 1.0f) {
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		} else {
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		}
		g2d.drawImage(fragment.getImage(declaration.getLayerType()), 0, 0, null);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
	}

	// TODO: is this transformation correct?
	public void initImageLayerMatrix(double scale, AffineTransform layerMatrix) {
		imageLayerMatrix.setTransform(layerMatrix);
		imageLayerMatrix.scale(scale, scale);
	}
}
