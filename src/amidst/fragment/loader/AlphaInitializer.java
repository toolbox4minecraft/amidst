package amidst.fragment.loader;

import amidst.fragment.layer.LayerDeclaration;
import amidst.map.Fragment;

public class AlphaInitializer extends FragmentLoader {
	public AlphaInitializer(LayerDeclaration declaration) {
		super(declaration);
	}

	@Override
	public void load(Fragment fragment) {
		fragment.initAlpha();
	}

	@Override
	public void reload(Fragment fragment) {
		// noop
	}
}
