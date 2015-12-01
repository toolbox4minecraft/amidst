package amidst.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import amidst.fragment.constructor.FragmentConstructor;
import amidst.fragment.drawer.FragmentDrawer;
import amidst.fragment.loader.FragmentLoader;
import amidst.map.layer.Layer;
import amidst.map.layer.LayerType;

public class LayerContainer {
	private final List<Layer> layers;
	private final boolean[] invalidatedLayers;
	private final List<FragmentConstructor> constructors;
	private final List<FragmentLoader> loaders;
	private final List<FragmentDrawer> drawers;

	public LayerContainer(Layer... layers) {
		this.layers = Collections.unmodifiableList(Arrays.asList(layers));
		invalidatedLayers = new boolean[layers.length];
		List<FragmentConstructor> constructor = new ArrayList<FragmentConstructor>(
				layers.length);
		List<FragmentLoader> loader = new ArrayList<FragmentLoader>(
				layers.length);
		List<FragmentDrawer> drawer = new ArrayList<FragmentDrawer>(
				layers.length);
		for (Layer layer : layers) {
			constructor.add(layer.getFragmentConstructor());
			loader.add(layer.getFragmentLoader());
			drawer.add(layer.getFragmentDrawer());
		}
		this.constructors = Collections.unmodifiableList(constructor);
		this.loaders = Collections.unmodifiableList(loader);
		this.drawers = Collections.unmodifiableList(drawer);
	}

	public List<Layer> getAllLayers() {
		return layers;
	}

	public void clearInvalidatedLayers() {
		for (int i = 0; i < invalidatedLayers.length; i++) {
			invalidatedLayers[i] = false;
		}
	}

	public void invalidateLayer(LayerType layerType) {
		invalidatedLayers[layerType.ordinal()] = true;
	}

	public void constructAll(Fragment fragment) {
		for (FragmentConstructor constructor : constructors) {
			constructor.construct(fragment);
		}
	}

	public void loadAll(Fragment fragment) {
		for (FragmentLoader loader : loaders) {
			loader.load(fragment);
		}
	}

	public void reloadInvalidated(Fragment fragment) {
		for (int i = 0; i < loaders.size(); i++) {
			if (invalidatedLayers[i]) {
				loaders.get(i).reload(fragment);
			}
		}
	}
}
