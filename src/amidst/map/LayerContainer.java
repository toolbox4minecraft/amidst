package amidst.map;

import amidst.map.layer.BiomeLayer;
import amidst.map.layer.IconLayer;
import amidst.map.layer.ImageLayer;
import amidst.map.layer.LiveLayer;
import amidst.map.layer.PlayerLayer;
import amidst.map.layer.StrongholdLayer;

public class LayerContainer {
	private PlayerLayer playerLayer;
	private StrongholdLayer strongholdLayer;
	private BiomeLayer biomeLayer;
	private ImageLayer[] imageLayers;
	private LiveLayer[] liveLayers;
	private IconLayer[] iconLayers;

	public LayerContainer(PlayerLayer playerLayer,
			StrongholdLayer strongholdLayer, BiomeLayer biomeLayer,
			ImageLayer[] imageLayers, LiveLayer[] liveLayers,
			IconLayer[] iconLayers) {
		this.playerLayer = playerLayer;
		this.strongholdLayer = strongholdLayer;
		this.biomeLayer = biomeLayer;
		this.imageLayers = imageLayers;
		this.liveLayers = liveLayers;
		this.iconLayers = iconLayers;
	}

	public PlayerLayer getPlayerLayer() {
		return playerLayer;
	}

	public StrongholdLayer getStrongholdLayer() {
		return strongholdLayer;
	}

	public BiomeLayer getBiomeLayer() {
		return biomeLayer;
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
