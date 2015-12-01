package amidst.map;

import amidst.minecraft.world.World;

public class MapBuilder {
	private final FragmentCache fragmentCache;

	public MapBuilder(FragmentCache fragmentCache) {
		this.fragmentCache = fragmentCache;
	}

	public Map construct(World world, MapZoom mapZoom,
			BiomeSelection biomeSelection) {
		return new Map(fragmentCache, world, mapZoom, biomeSelection);
	}
}
