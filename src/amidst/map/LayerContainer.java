package amidst.map;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Set;

import amidst.map.layer.IconLayer;
import amidst.map.layer.ImageLayer;
import amidst.map.layer.Layer;
import amidst.map.layer.LayerType;
import amidst.map.layer.LiveLayer;
import amidst.minecraft.world.World;

public class LayerContainer {
	private java.util.Map<LayerType, Layer> layerMap = new EnumMap<LayerType, Layer>(
			LayerType.class);
	private Set<LayerType> loadableLayerTypes = EnumSet.noneOf(LayerType.class);

	private ImageLayer[] imageLayers;
	private LiveLayer[] liveLayers;
	private IconLayer[] iconLayers;

	public LayerContainer(ImageLayer[] imageLayers, LiveLayer[] liveLayers,
			IconLayer[] iconLayers) {
		this.imageLayers = imageLayers;
		this.liveLayers = liveLayers;
		this.iconLayers = iconLayers;
		initLayerMap();
		initLoadableLayerTypes();
	}

	private void initLayerMap() {
		for (ImageLayer layer : imageLayers) {
			layerMap.put(layer.getLayerType(), layer);
		}
		for (LiveLayer layer : liveLayers) {
			layerMap.put(layer.getLayerType(), layer);
		}
		for (IconLayer layer : iconLayers) {
			layerMap.put(layer.getLayerType(), layer);
		}
	}

	private void initLoadableLayerTypes() {
		for (ImageLayer layer : imageLayers) {
			loadableLayerTypes.add(layer.getLayerType());
		}
		for (IconLayer layer : iconLayers) {
			loadableLayerTypes.add(layer.getLayerType());
		}
	}

	public ImageLayer[] getImageLayers() {
		return imageLayers;
	}

	public LiveLayer[] getLiveLayers() {
		return liveLayers;
	}

	public IconLayer[] getIconLayers() {
		return iconLayers;
	}

	public void setMap(Map map) {
		for (ImageLayer layer : imageLayers) {
			layer.setMap(map);
		}
		for (LiveLayer layer : liveLayers) {
			layer.setMap(map);
		}
		for (IconLayer layer : iconLayers) {
			layer.setMap(map);
		}
	}

	public void setWorld(World world) {
		for (ImageLayer layer : imageLayers) {
			layer.setWorld(world);
		}
		for (LiveLayer layer : liveLayers) {
			layer.setWorld(world);
		}
		for (IconLayer layer : iconLayers) {
			layer.setWorld(world);
		}
	}

	public Layer getLayer(LayerType layerType) {
		return layerMap.get(layerType);
	}

	public Set<LayerType> getLoadableLayerTypes() {
		return loadableLayerTypes;
	}
}
