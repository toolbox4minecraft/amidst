package amidst.map;

import amidst.minecraft.world.World;

public class MapFactory {
	private final LayerContainerFactory layerContainerFactory;
	private final FragmentManager fragmentManager;

	public MapFactory(LayerContainerFactory layerContainerFactory) {
		this.layerContainerFactory = layerContainerFactory;
		this.fragmentManager = new FragmentManager(
				layerContainerFactory.getConstructors());
	}

	public Map create(World world, MapZoom mapZoom,
			BiomeSelection biomeSelection) {
		return new Map(mapZoom, biomeSelection, fragmentManager,
				layerContainerFactory, world);
	}
}
