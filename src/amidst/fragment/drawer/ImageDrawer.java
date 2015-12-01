package amidst.fragment.drawer;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

import amidst.map.Fragment;
import amidst.map.layer.LayerType;
import amidst.minecraft.world.Resolution;

public class ImageDrawer implements FragmentDrawer {
	private final AffineTransform imageLayerMatrix = new AffineTransform();
	private final LayerType layerType;
	private final Resolution resolution;

	public ImageDrawer(LayerType layerType, Resolution resolution) {
		this.layerType = layerType;
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
		g2d.drawImage(fragment.getImage(layerType), 0, 0, null);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
	}

	// TODO: is this transformation correct?
	public void initImageLayerMatrix(double scale, AffineTransform layerMatrix) {
		imageLayerMatrix.setTransform(layerMatrix);
		imageLayerMatrix.scale(scale, scale);
	}
}
