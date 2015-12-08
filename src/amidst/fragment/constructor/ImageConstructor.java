package amidst.fragment.constructor;

import java.awt.image.BufferedImage;

import amidst.fragment.Fragment;
import amidst.mojangapi.world.Resolution;

public class ImageConstructor implements FragmentConstructor {
	private final int size;
	private final int layerId;

	public ImageConstructor(Resolution resolution, int layerId) {
		this.size = resolution.getStepsPerFragment();
		this.layerId = layerId;
	}

	@Override
	public void construct(Fragment fragment) {
		fragment.putImage(layerId, createBufferedImage());
	}

	private BufferedImage createBufferedImage() {
		return new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
	}
}
