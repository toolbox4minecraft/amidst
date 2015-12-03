package amidst.fragment.loader;

import amidst.fragment.layer.LayerDeclaration;
import amidst.map.FragmentGraphItem;

public abstract class FragmentLoader {
	protected final LayerDeclaration declaration;

	public FragmentLoader(LayerDeclaration declaration) {
		this.declaration = declaration;
	}

	public LayerDeclaration getLayerDeclaration() {
		return declaration;
	}

	public abstract void load(FragmentGraphItem fragment);

	public abstract void reload(FragmentGraphItem fragment);
}
