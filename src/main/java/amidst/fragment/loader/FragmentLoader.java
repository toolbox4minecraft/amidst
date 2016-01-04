package amidst.fragment.loader;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.Fragment;
import amidst.fragment.layer.LayerDeclaration;

@NotThreadSafe
public abstract class FragmentLoader {
	protected final LayerDeclaration declaration;

	public FragmentLoader(LayerDeclaration declaration) {
		this.declaration = declaration;
	}

	public LayerDeclaration getLayerDeclaration() {
		return declaration;
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	public abstract void load(Fragment fragment);

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	public abstract void reload(Fragment fragment);
}
