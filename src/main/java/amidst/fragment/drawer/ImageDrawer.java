package amidst.fragment.drawer;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.Fragment;
import amidst.fragment.layer.LayerDeclaration;
import amidst.gui.main.viewer.Graphics2DAccelerationCounter;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.settings.Setting;

@NotThreadSafe
public class ImageDrawer extends FragmentDrawer {
	private final Resolution resolution;
	private final Graphics2DAccelerationCounter accelerationCounter;
	private final Setting<Boolean> useHybridScaling;

	public ImageDrawer(
			LayerDeclaration declaration,
			Resolution resolution,
			Graphics2DAccelerationCounter accelerationCounter,
			Setting<Boolean> useHybridScaling) {
		super(declaration);
		this.resolution = resolution;
		this.accelerationCounter = accelerationCounter;
		this.useHybridScaling = useHybridScaling;
	}

	@Override
	public void draw(Fragment fragment, Graphics2D g2d, float time) {
		if (useHybridScaling.get()) {
			drawHybrid(fragment, g2d, time);
		} else {
			drawNearest(fragment, g2d, time);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void drawNearest(Fragment fragment, Graphics2D g2d, float time) {
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

	private static final GraphicsConfiguration GC = GraphicsEnvironment.getLocalGraphicsEnvironment()
																	   .getDefaultScreenDevice()
																	   .getDefaultConfiguration();

	private VolatileImage tempImage;

	/**
	 * This sets the amount of deviation in the pixels that nearest
	 * neighbor can have.
	 */
	private static final double NEAREST_PIXEL_THRESHOLD = 1.0d;

	/**
	 * When drawing with this method, we scale the image with a hybrid of
	 * both nearest neighbor and bilinear. As an example, lets say that
	 * the scale of the g2d is 3.2. We want to split the 3.2 into 3.0 and
	 * 0.2. The nearest neighbor algorithm then scales it up by 3.0 and
	 * stores it into a temporary image. Then, the bilinear algorithm
	 * scales up the temporary image by whatever it needs to get 3.0 to
	 * 3.2. In this case, it would be 1.066 repeating.
	 * 
	 * This gives the upsides of both nearest neighbor for image
	 * preservation and bilinear for smooth edges at irregular scales.
	 */
	@CalledOnlyBy(AmidstThread.EDT)
	private void drawHybrid(Fragment fragment, Graphics2D g2d, float time) {
		int scale = resolution.getStep();
		g2d.scale(scale, scale);
		
		Object oldHint = g2d.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
		
		Image image = fragment.getImage(declaration.getLayerId());
		
		double scaleX = g2d.getTransform().getScaleX(); // This could be the Y value, but it shouldn't matter because they should be equal
		double nearestScale = Math.floor(scaleX); // scale value for nearest neighbor pass (if applicable)
		int imageWidth = image.getWidth(null);
		
		// this checks to see if the scale lies within the amount that the scale can deviate before changing pixels
		double threshold = ((NEAREST_PIXEL_THRESHOLD / imageWidth) * nearestScale) / scale;
		double check = Math.abs(scaleX - Math.round(scaleX)); // works for checking above or below
		
		if(check > threshold) {
			if (scaleX > 2.0) { // this algorithm has no benefit if the scale is less than 2
				double bilinearScale = scaleX / nearestScale; // scale value for bilinear pass
				
				int nearestSize = (int) (imageWidth * nearestScale);
				// recreate volatile image if it's been messed up in some way
				if (tempImage == null || tempImage.getWidth() != nearestSize || tempImage.validate(GC) == VolatileImage.IMAGE_INCOMPATIBLE) {
					tempImage = GC.createCompatibleVolatileImage(nearestSize, nearestSize, Transparency.TRANSLUCENT);
				}
				
				// create a g2d for the temporary image and scales the original with nearest neighbor into it
				Graphics2D g2dTemp = tempImage.createGraphics();
				g2dTemp.setComposite(AlphaComposite.Src); // fixes the transparency being wrong
				g2dTemp.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				g2dTemp.scale(nearestScale, nearestScale);
				accelerationCounter.log(image);
				g2dTemp.drawImage(image, 0, 0, null); 
				g2dTemp.dispose();
				
				// set them main g2d to be ready to apply the bilinear scaling
				// for some reason, g2d doesn't let us directly change the transform's variables easily
				// this means that we have to multiply our bilinear scale by the inverse of the current scale to cancel out the current scale
				double bilinearScaleModified = (1 / scaleX) * bilinearScale;
				g2d.scale(bilinearScaleModified, bilinearScaleModified);
				
				image = tempImage;
			}
			// if the scale isn't an integer, set the main g2d to bilinear
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		} else {
			// if nearest neighbor is within the threshold to where it wouldn't badly modify any pixels, use it
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		}
				
		accelerationCounter.log(image);
		g2d.drawImage(image, 0, 0, null);
		
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, oldHint);
	}

}
