package amidst.fragment.drawer;

import java.awt.Graphics2D;
import java.awt.RenderingHints;

import amidst.fragment.layer.LayerDeclaration;
import amidst.map.Fragment;
import amidst.minecraft.world.Resolution;

public class ImageDrawer extends FragmentDrawer {
	private final Resolution resolution;

	public ImageDrawer(LayerDeclaration declaration, Resolution resolution) {
		super(declaration);
		this.resolution = resolution;
	}

	@Override
	public void draw(Fragment fragment, Graphics2D g2d) {
		g2d.scale(resolution.getStep(), resolution.getStep());
		if (g2d.getTransform().getScaleX() < 1.0f) {
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		} else {
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		}
		g2d.drawImage(fragment.getImage(declaration.getLayerId()), 0, 0, null);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
	}
}
