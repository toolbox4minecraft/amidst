package amidst.map;

import java.util.Collection;
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
	private EnumMap<LayerType, Layer> layerMap = new EnumMap<LayerType, Layer>(
			LayerType.class);
	private Set<LayerType> allLayerTypes = EnumSet.noneOf(LayerType.class);
	private Set<LayerType> imageLayerTypes = EnumSet.noneOf(LayerType.class);
	private Set<LayerType> liveLayerTypes = EnumSet.noneOf(LayerType.class);
	private Set<LayerType> iconLayerTypes = EnumSet.noneOf(LayerType.class);
	private Set<LayerType> invalidatedLayerTypes = EnumSet
			.noneOf(LayerType.class);

	private ImageLayer[] imageLayers;
	private LiveLayer[] liveLayers;
	private IconLayer[] iconLayers;

	public LayerContainer(ImageLayer[] imageLayers, LiveLayer[] liveLayers,
			IconLayer[] iconLayers) {
		this.imageLayers = imageLayers;
		this.liveLayers = liveLayers;
		this.iconLayers = iconLayers;
		initLayerMap();
		initLayerTypes();
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

	private void initLayerTypes() {
		for (ImageLayer layer : imageLayers) {
			imageLayerTypes.add(layer.getLayerType());
			allLayerTypes.add(layer.getLayerType());
		}
		for (LiveLayer layer : liveLayers) {
			liveLayerTypes.add(layer.getLayerType());
			allLayerTypes.add(layer.getLayerType());
		}
		for (IconLayer layer : iconLayers) {
			iconLayerTypes.add(layer.getLayerType());
			allLayerTypes.add(layer.getLayerType());
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

	public Set<LayerType> getAllLayerTypes() {
		return allLayerTypes;
	}

	public Collection<Layer> getAllLayers() {
		return layerMap.values();
	}

	public void clearInvalidatedLayerTypes() {
		invalidatedLayerTypes.clear();
	}

	public Set<LayerType> getInvalidatedLayerTypes() {
		return invalidatedLayerTypes;
	}

	public void invalidateLayer(LayerType layerType) {
		invalidatedLayerTypes.add(layerType);
	}

	public ImageLayer getImageLayer(LayerType layerType) {
		Layer layer = layerMap.get(layerType);
		if (layer instanceof ImageLayer) {
			return (ImageLayer) layer;
		} else {
			throw createWrongLayerTypeException(layerType, "image");
		}
	}

	public LiveLayer getLiveLayer(LayerType layerType) {
		Layer layer = layerMap.get(layerType);
		if (layer instanceof LiveLayer) {
			return (LiveLayer) layer;
		} else {
			throw createWrongLayerTypeException(layerType, "live");
		}
	}

	public IconLayer getIconLayer(LayerType layerType) {
		Layer layer = layerMap.get(layerType);
		if (layer instanceof IconLayer) {
			return (IconLayer) layer;
		} else {
			throw createWrongLayerTypeException(layerType, "icon");
		}
	}

	private IllegalArgumentException createWrongLayerTypeException(
			LayerType layerType, String expected) {
		return new IllegalArgumentException("Wrong layer type. " + layerType
				+ " is not an " + expected
				+ " layer. This should never happen!");
	}
}
