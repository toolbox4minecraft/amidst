package amidst.map;

import amidst.minecraft.world.World;

public class MapBuilder {
	private final FragmentCache fragmentCache;
	private final MapZoom mapZoom;
	private final BiomeSelection biomeSelection;

	public MapBuilder(FragmentCache fragmentCache, MapZoom mapZoom,
			BiomeSelection biomeSelection) {
		this.fragmentCache = fragmentCache;
		this.mapZoom = mapZoom;
		this.biomeSelection = biomeSelection;
	}

	public Map construct(World world) {
		return new Map(fragmentCache, world, mapZoom, biomeSelection);
	}
}
