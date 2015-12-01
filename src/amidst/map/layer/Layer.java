package amidst.map.layer;

import amidst.fragment.constructor.FragmentConstructor;
import amidst.fragment.drawer.FragmentDrawer;
import amidst.fragment.loader.FragmentLoader;
import amidst.map.LayerDeclaration;
import amidst.preferences.PrefModel;

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
		return declaration.getLayerType();
	}

	public boolean isVisible() {
		return declaration.getIsVisiblePreference().get();
	}

	public PrefModel<Boolean> getIsVisiblePreference() {
		return declaration.getIsVisiblePreference();
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
