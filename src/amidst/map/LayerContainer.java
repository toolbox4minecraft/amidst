package amidst.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

import amidst.fragment.constructor.FragmentConstructor;
import amidst.fragment.drawer.FragmentDrawer;
import amidst.fragment.loader.FragmentLoader;
import amidst.map.layer.Layer;
import amidst.map.layer.LayerType;

public class LayerContainer {
	private EnumMap<LayerType, Layer> layerMap = new EnumMap<LayerType, Layer>(
			LayerType.class);
	private final boolean[] invalidatedLayers;
	private final List<FragmentConstructor> constructors;
	private final List<FragmentLoader> loaders;
	private final List<FragmentDrawer> drawers;

	public LayerContainer(Layer... layers) {
		initLayerMap(layers);
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

	private void initLayerMap(Layer[] layers) {
		for (Layer layer : layers) {
			layerMap.put(layer.getLayerType(), layer);
		}
	}

	public Collection<Layer> getAllLayers() {
		return layerMap.values();
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
