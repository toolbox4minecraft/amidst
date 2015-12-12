package amidst.fragment.loader;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.Fragment;
import amidst.fragment.layer.LayerDeclaration;
import amidst.settings.Setting;

@NotThreadSafe
public class AlphaInitializer extends FragmentLoader {
	private final Setting<Boolean> fragmentFadingSetting;

	public AlphaInitializer(LayerDeclaration declaration,
			Setting<Boolean> fragmentFadingSetting) {
		super(declaration);
		this.fragmentFadingSetting = fragmentFadingSetting;
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	@Override
	public void load(Fragment fragment) {
		if (fragmentFadingSetting.get()) {
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
