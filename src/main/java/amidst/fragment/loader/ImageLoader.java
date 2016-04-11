package amidst.fragment.loader;

import java.awt.image.BufferedImage;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledByAny;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.Fragment;
import amidst.fragment.colorprovider.ColorProvider;
import amidst.fragment.layer.LayerDeclaration;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Resolution;

@NotThreadSafe
public class ImageLoader extends FragmentLoader {
	private final Resolution resolution;
	private final ColorProvider colorProvider;
	private final int size;
	private final int[] rgbArray;
	private volatile BufferedImage bufferedImage;

	@CalledByAny
	public ImageLoader(LayerDeclaration declaration, Resolution resolution, ColorProvider colorProvider) {
		super(declaration);
		this.resolution = resolution;
		this.colorProvider = colorProvider;
		this.size = resolution.getStepsPerFragment();
		this.rgbArray = new int[size * size];
		this.bufferedImage = createBufferedImage();
	}

	@CalledByAny
	private BufferedImage createBufferedImage() {
		return new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
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
		CoordinatesInWorld corner = fragment.getCorner();
		long cornerX = corner.getXAs(resolution);
		long cornerY = corner.getYAs(resolution);
		drawToCache(dimension, fragment, cornerX, cornerY);
		bufferedImage.setRGB(0, 0, size, size, rgbArray, 0, size);
		bufferedImage = fragment.getAndSetImage(declaration.getLayerId(), bufferedImage);
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	private void drawToCache(Dimension dimension, Fragment fragment, long cornerX, long cornerY) {
		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {
				int index = getCacheIndex(x, y);
				rgbArray[index] = colorProvider.getColorAt(dimension, fragment, cornerX, cornerY, x, y);
			}
		}
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	private int getCacheIndex(int x, int y) {
		return x + y * size;
	}
}
