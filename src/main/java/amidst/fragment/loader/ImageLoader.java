package amidst.fragment.loader;

import java.awt.image.BufferedImage;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledByAny;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.ThreadSafe;
import amidst.fragment.Fragment;
import amidst.fragment.colorprovider.ColorProvider;
import amidst.fragment.layer.LayerDeclaration;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Resolution;

@ThreadSafe
public class ImageLoader extends FragmentLoader {
	private final Resolution resolution;
	private final ColorProvider colorProvider;
	private final int width, height;

	@CalledByAny
	public ImageLoader(LayerDeclaration declaration, Resolution resolution, ColorProvider colorProvider) {
		super(declaration);
		this.resolution = resolution;
		this.colorProvider = colorProvider;
		this.width = resolution.getStepsPerFragment();
		this.height = resolution.getStepsPerFragment();
	}

	@CalledByAny
	private BufferedImage createBufferedImage() {
		return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	@Override
	public void load(Dimension dimension, Fragment fragment) {
		doLoad(dimension, fragment);
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	@Override
	public void reload(Dimension dimension, Fragment fragment) {
		doLoad(dimension, fragment);
	}
	
	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	private void doLoad(Dimension dimension, Fragment fragment) {
		// Slightly faster and uses less ram, but not as safe
		CoordinatesInWorld corner = fragment.getCorner();
		long cornerX = corner.getXAs(resolution);
		long cornerY = corner.getYAs(resolution);
		drawToImage(dimension, fragment, cornerX, cornerY, fragment.getImage(declaration.getLayerId()));
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	private void doLoadAtomic(Dimension dimension, Fragment fragment) {
		CoordinatesInWorld corner = fragment.getCorner();
		long cornerX = corner.getXAs(resolution);
		long cornerY = corner.getYAs(resolution);
		BufferedImage tempImg = createBufferedImage();
		drawToImage(dimension, fragment, cornerX, cornerY, tempImg);
		fragment.putImage(declaration.getLayerId(), tempImg);
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	private void drawToImage(Dimension dimension, Fragment fragment, long cornerX, long cornerY, BufferedImage bufferedImage) {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				bufferedImage.setRGB(x, y, colorProvider.getColorAt(dimension, fragment, cornerX, cornerY, x, y));
			}
		}
	}
}
