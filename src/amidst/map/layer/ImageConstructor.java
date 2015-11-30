package amidst.map.layer;

import java.awt.image.BufferedImage;

import amidst.map.Fragment;
import amidst.minecraft.world.Resolution;

public class ImageConstructor implements FragmentConstructor {
	private final LayerType layerType;
	protected final int size;

	public ImageConstructor(LayerType layerType, Resolution resolution) {
		this.layerType = layerType;
		this.size = resolution.getStepsPerFragment();
	}

	@Override
	public void construct(Fragment fragment) {
		fragment.putImage(layerType, createBufferedImage());
	}

	private BufferedImage createBufferedImage() {
		return new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
	}
}
