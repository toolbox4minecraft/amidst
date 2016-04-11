package amidst.fragment.drawer;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.Fragment;
import amidst.fragment.layer.LayerDeclaration;
import amidst.gui.main.viewer.Graphics2DAccelerationCounter;
import amidst.mojangapi.world.coordinates.Resolution;

@NotThreadSafe
public class ImageDrawer extends FragmentDrawer {
	private final Resolution resolution;
	private final Graphics2DAccelerationCounter accelerationCounter;

	public ImageDrawer(
			LayerDeclaration declaration,
			Resolution resolution,
			Graphics2DAccelerationCounter accelerationCounter) {
		super(declaration);
		this.resolution = resolution;
		this.accelerationCounter = accelerationCounter;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	public void draw(Fragment fragment, Graphics2D g2d, float time) {
		int scale = resolution.getStep();
		g2d.scale(scale, scale);
		Object oldHint = g2d.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
		Object newHint = getRenderingHint(g2d);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, newHint);
		BufferedImage image = fragment.getImage(declaration.getLayerId());
		accelerationCounter.log(image);
		g2d.drawImage(image, 0, 0, null);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, oldHint);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private Object getRenderingHint(Graphics2D g2d) {
		if (g2d.getTransform().getScaleX() < 1.0f) {
			return RenderingHints.VALUE_INTERPOLATION_BILINEAR;
		} else {
			return RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
		}
	}
}
