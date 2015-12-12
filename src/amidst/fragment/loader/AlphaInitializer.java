package amidst.fragment.loader;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.Fragment;
import amidst.fragment.layer.LayerDeclaration;
import amidst.settings.Setting;

@NotThreadSafe
public class AlphaInitializer extends FragmentLoader {
	private final Setting<Boolean> fragmentFadingPreference;

	public AlphaInitializer(LayerDeclaration declaration,
			Setting<Boolean> fragmentFadingPreference) {
		super(declaration);
		this.fragmentFadingPreference = fragmentFadingPreference;
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	@Override
	public void load(Fragment fragment) {
		if (fragmentFadingPreference.get()) {
			fragment.setAlpha(0.0f);
		} else {
			fragment.setAlpha(1.0f);
		}
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	@Override
	public void reload(Fragment fragment) {
		// noop
	}
}
