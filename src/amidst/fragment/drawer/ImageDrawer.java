package amidst.fragment.drawer;

import java.awt.Graphics2D;
import java.awt.RenderingHints;

import amidst.fragment.Fragment;
import amidst.fragment.layer.LayerDeclaration;
import amidst.mojangapi.world.Resolution;

public class ImageDrawer extends FragmentDrawer {
	private final Resolution resolution;

	public ImageDrawer(LayerDeclaration declaration, Resolution resolution) {
		super(declaration);
		this.resolution = resolution;
	}

	@Override
	public void draw(Fragment fragment, Graphics2D g2d, float time) {
		int scale = resolution.getStep();
		g2d.scale(scale, scale);
		Object oldHint = g2d.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
		Object newHint = getRenderingHint(g2d);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, newHint);
		g2d.drawImage(fragment.getImage(declaration.getLayerId()), 0, 0, null);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, oldHint);
	}

	private Object getRenderingHint(Graphics2D g2d) {
		if (g2d.getTransform().getScaleX() < 1.0f) {
			return RenderingHints.VALUE_INTERPOLATION_BILINEAR;
		} else {
			return RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
		}
	}
}
