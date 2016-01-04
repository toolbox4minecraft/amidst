package amidst.fragment.constructor;

import java.awt.image.BufferedImage;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.Immutable;
import amidst.fragment.Fragment;
import amidst.mojangapi.world.coordinates.Resolution;

@Immutable
public class ImageConstructor implements FragmentConstructor {
	private final int size;
	private final int layerId;

	@CalledOnlyBy(AmidstThread.EDT)
	public ImageConstructor(Resolution resolution, int layerId) {
		this.size = resolution.getStepsPerFragment();
		this.layerId = layerId;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	public void construct(Fragment fragment) {
		fragment.putImage(layerId, createBufferedImage());
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private BufferedImage createBufferedImage() {
		return new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
	}
}
