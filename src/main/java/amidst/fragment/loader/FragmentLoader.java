package amidst.fragment.loader;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.Fragment;
import amidst.fragment.layer.LayerDeclaration;
import amidst.mojangapi.world.Dimension;

@NotThreadSafe
public abstract class FragmentLoader {
	protected final LayerDeclaration declaration;

	public FragmentLoader(LayerDeclaration declaration) {
		this.declaration = declaration;
	}

	public int getLayerId() {
		return declaration.getLayerId();
	}

	public boolean isEnabled() {
		return declaration.isVisible();
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	public abstract void load(Dimension dimension, Fragment fragment);

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	public abstract void reload(Dimension dimension, Fragment fragment);
}
