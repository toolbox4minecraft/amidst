package amidst.map.layer;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import amidst.map.Fragment;
import amidst.minecraft.world.CoordinatesInWorld;
import amidst.minecraft.world.Resolution;

public abstract class ImageLayer extends Layer {
	private final Resolution resolution;
	private final AffineTransform imageLayerMatrix = new AffineTransform();

	public ImageLayer(LayerType layerType, Resolution resolution) {
		super(layerType);
		this.resolution = resolution;
	}

	public Resolution getResolution() {
		return resolution;
	}

	public int getSize() {
		return Fragment.SIZE / resolution.getStep();
	}

	public double getScale() {
		return resolution.getStep();
	}

	@Override
	public void load(Fragment fragment, int[] imageCache) {
		doLoad(fragment, imageCache);
	}

	@Override
	public void reload(Fragment fragment, int[] imageCache) {
		doLoad(fragment, imageCache);
	}

	protected void doLoad(Fragment fragment, int[] imageCache) {
		CoordinatesInWorld corner = fragment.getCorner();
		long cornerX = corner.getXAs(resolution);
		long cornerY = corner.getYAs(resolution);
		int size = getSize();
		drawToCache(fragment, imageCache, cornerX, cornerY, size);
		BufferedImage image = fragment.getImage(getLayerType());
		image.setRGB(0, 0, size, size, imageCache, 0, size);
	}

	protected void drawToCache(Fragment fragment, int[] cache, long cornerX,
			long cornerY, int size) {
		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {
				int index = getCacheIndex(x, y, size);
				cache[index] = getColorAt(fragment, cornerX, cornerY, x, y);
			}
		}
	}

	private int getCacheIndex(int x, int y, int size) {
		return x + y * size;
	}

	@Override
	public void draw(Fragment fragment, Graphics2D g2d,
			AffineTransform layerMatrix) {
		if (fragment.isLoaded()) {
			initImageLayerDrawMatrix(getScale(), layerMatrix);
			g2d.setTransform(imageLayerMatrix);
			if (g2d.getTransform().getScaleX() < 1.0f) {
				g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
						RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			} else {
				g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
						RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			}
			g2d.drawImage(fragment.getImage(getLayerType()), 0, 0, null);
		}
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
	}

	// TODO: is this transformation correct?
	public void initImageLayerDrawMatrix(double scale,
			AffineTransform layerMatrix) {
		imageLayerMatrix.setTransform(layerMatrix);
		imageLayerMatrix.scale(scale, scale);
	}

	protected abstract int getColorAt(Fragment fragment, long cornerX,
			long cornerY, int x, int y);
}
