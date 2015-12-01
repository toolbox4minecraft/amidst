package amidst.fragment.constructor;

import java.awt.image.BufferedImage;

import amidst.map.Fragment;
import amidst.map.LayerDeclaration;
import amidst.minecraft.world.Resolution;

public class ImageConstructor implements FragmentConstructor {
	private final LayerDeclaration declaration;
	protected final int size;

	public ImageConstructor(LayerDeclaration declaration, Resolution resolution) {
		this.declaration = declaration;
		this.size = resolution.getStepsPerFragment();
	}

	@Override
	public void construct(Fragment fragment) {
		fragment.putImage(declaration.getLayerType(), createBufferedImage());
	}

	private BufferedImage createBufferedImage() {
		return new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
	}
}
