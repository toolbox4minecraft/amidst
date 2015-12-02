package amidst.map;

import amidst.fragment.layer.LayerManagerFactory;
import amidst.minecraft.world.World;

public class MapBuilder {
	private final LayerManagerFactory layerManagerFactory;
	private final FragmentManager fragmentManager;

	public MapBuilder(LayerManagerFactory layerManagerFactory) {
		this.layerManagerFactory = layerManagerFactory;
		this.fragmentManager = new FragmentManager(
				layerManagerFactory.getConstructors(),
				layerManagerFactory.getNumberOfLayers());
	}

	public Map create(World world, MapZoom mapZoom,
			BiomeSelection biomeSelection) {
		return new Map(mapZoom, biomeSelection, fragmentManager,
				layerManagerFactory, world);
	}
}
