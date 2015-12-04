package amidst.fragment.loader;

import amidst.fragment.layer.LayerDeclaration;
import amidst.map.Fragment;
import amidst.preferences.PrefModel;

public class AlphaInitializer extends FragmentLoader {
	private final PrefModel<Boolean> mapFadingPreference;

	public AlphaInitializer(LayerDeclaration declaration,
			PrefModel<Boolean> mapFadingPreference) {
		super(declaration);
		this.mapFadingPreference = mapFadingPreference;
	}

	@Override
	public void load(Fragment fragment) {
		if (mapFadingPreference.get()) {
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
