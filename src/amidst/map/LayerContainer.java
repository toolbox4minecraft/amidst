package amidst.map;

import java.util.Collection;
import java.util.EnumMap;

import amidst.map.layer.Layer;
import amidst.map.layer.LayerType;

public class LayerContainer {
	private EnumMap<LayerType, Layer> layerMap = new EnumMap<LayerType, Layer>(
			LayerType.class);
	private EnumMap<LayerType, Layer> invalidatedLayerMap = new EnumMap<LayerType, Layer>(
			LayerType.class);

	public LayerContainer(Layer... layers) {
		initLayerMap(layers);
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
		invalidatedLayerMap.clear();
	}

	public void invalidateLayer(LayerType layerType) {
		invalidatedLayerMap.put(layerType, layerMap.get(layerType));
	}

	public void constructAll(Fragment fragment) {
		for (Layer layer : layerMap.values()) {
			layer.construct(fragment);
		}
	}

	public void loadAll(Fragment fragment) {
		for (Layer layer : layerMap.values()) {
			layer.load(fragment);
		}
	}

	public void reloadInvalidated(Fragment fragment) {
		for (Layer layer : invalidatedLayerMap.values()) {
			layer.reload(fragment);
		}
	}
}
