package amidst.map;

import amidst.map.layers.IconLayer;
import amidst.map.layers.ImageLayer;
import amidst.map.layers.LiveLayer;

public class LayerContainer {
	private ImageLayer[] imageLayers;
	private LiveLayer[] liveLayers;
	private IconLayer[] iconLayers;

	public LayerContainer(ImageLayer[] imageLayers, LiveLayer[] liveLayers,
			IconLayer[] iconLayers) {
		this.imageLayers = imageLayers;
		this.liveLayers = liveLayers;
		this.iconLayers = iconLayers;
		initImageLayerIds();
	}

	private void initImageLayerIds() {
		for (int i = 0; i < imageLayers.length; i++) {
			imageLayers[i].setLayerId(i);
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

	public void updateAllLayers(float time) {
		for (ImageLayer layer : imageLayers) {
			layer.update(time);
		}
		for (LiveLayer layer : liveLayers) {
			layer.update(time);
		}
		for (IconLayer layer : iconLayers) {
			layer.update(time);
		}
	}

	public void reloadAllLayers(Map map) {
		for (ImageLayer layer : imageLayers) {
			layer.setMap(map);
			layer.reload();
		}
		for (LiveLayer layer : liveLayers) {
			layer.setMap(map);
			layer.reload();
		}
		for (IconLayer layer : iconLayers) {
			layer.setMap(map);
			layer.reload();
		}
	}
}
