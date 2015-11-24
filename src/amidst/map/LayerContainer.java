package amidst.map;

import java.util.Collection;
import java.util.EnumMap;

import amidst.map.layer.Layer;
import amidst.map.layer.LayerType;
import amidst.minecraft.world.World;

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

	public void setMap(Map map) {
		for (Layer layer : getAllLayers()) {
			layer.setMap(map);
		}
	}

	public void setWorld(World world) {
		for (Layer layer : getAllLayers()) {
			layer.setWorld(world);
		}
	}

	public Collection<Layer> getAllLayers() {
		return layerMap.values();
	}

	public void clearInvalidatedLayers() {
		invalidatedLayerMap.clear();
	}

	public Collection<Layer> getInvalidatedLayers() {
		return invalidatedLayerMap.values();
	}

	public void invalidateLayer(LayerType layerType) {
		invalidatedLayerMap.put(layerType, layerMap.get(layerType));
	}
}
