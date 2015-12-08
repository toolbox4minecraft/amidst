package amidst.fragment.loader;

import amidst.fragment.Fragment;
import amidst.fragment.layer.LayerDeclaration;
import amidst.preferences.PrefModel;

public class AlphaInitializer extends FragmentLoader {
	private final PrefModel<Boolean> fragmentFadingPreference;

	public AlphaInitializer(LayerDeclaration declaration,
			PrefModel<Boolean> fragmentFadingPreference) {
		super(declaration);
		this.fragmentFadingPreference = fragmentFadingPreference;
	}

	@Override
	public void load(Fragment fragment) {
		if (fragmentFadingPreference.get()) {
			fragment.setAlpha(0.0f);
		} else {
			fragment.setAlpha(1.0f);
		}
	}

	@Override
	public void reload(Fragment fragment) {
		// noop
	}
}
