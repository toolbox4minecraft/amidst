package amidst.map.layer;

import amidst.fragment.constructor.FragmentConstructor;
import amidst.fragment.drawer.FragmentDrawer;
import amidst.fragment.loader.FragmentLoader;
import amidst.map.LayerDeclaration;

public class Layer {
	private final LayerDeclaration declaration;
	private final FragmentConstructor constructor;
	private final FragmentLoader loader;
	private final FragmentDrawer drawer;

	public Layer(LayerDeclaration declaration, FragmentConstructor constructor,
			FragmentLoader loader, FragmentDrawer drawer) {
		this.declaration = declaration;
		this.constructor = constructor;
		this.loader = loader;
		this.drawer = drawer;
	}

	public LayerType getLayerType() {
		return getLayerDeclaration().getLayerType();
	}

	public boolean isVisible() {
		return getLayerDeclaration().getIsVisiblePreference().get();
	}

	public LayerDeclaration getLayerDeclaration() {
		return declaration;
	}

	public FragmentConstructor getFragmentConstructor() {
		return constructor;
	}

	public FragmentLoader getFragmentLoader() {
		return loader;
	}

	public FragmentDrawer getFragmentDrawer() {
		return drawer;
	}
}
