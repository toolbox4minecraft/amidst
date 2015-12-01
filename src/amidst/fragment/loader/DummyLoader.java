package amidst.fragment.loader;

import amidst.fragment.layer.LayerDeclaration;
import amidst.map.Fragment;

public class DummyLoader extends FragmentLoader {
	public DummyLoader(LayerDeclaration declaration) {
		super(declaration);
	}

	@Override
	public void load(Fragment fragment) {
		// noop
	}

	@Override
	public void reload(Fragment fragment) {
		// noop
	}
}
