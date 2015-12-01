package amidst.map;

import amidst.minecraft.world.World;

public class MapBuilder {
	private final LayerContainerFactory layerContainerFactory;
	private final FragmentManager fragmentManager;

	public MapBuilder(LayerContainerFactory layerContainerFactory) {
		this.layerContainerFactory = layerContainerFactory;
		this.fragmentManager = new FragmentManager(
				layerContainerFactory.getConstructors());
	}

	public Map construct(World world, MapZoom mapZoom,
			BiomeSelection biomeSelection) {
		return new Map(mapZoom, biomeSelection, fragmentManager,
				layerContainerFactory, world);
	}
}
