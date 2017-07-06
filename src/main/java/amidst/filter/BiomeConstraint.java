package amidst.filter;

import java.util.Optional;

import amidst.documentation.Immutable;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.biome.BiomeData;
import amidst.mojangapi.world.coordinates.Coordinates;
import amidst.mojangapi.world.coordinates.Region;
import amidst.mojangapi.world.coordinates.Resolution;


@Immutable
public class BiomeConstraint implements Constraint {
	
	private final Region region;
	private final Biome biome;
	private final boolean checkDistance;
	
	public BiomeConstraint(Region region, Biome biome, boolean check) {
		this.region = region;
		this.biome = biome;
		checkDistance = check;
	}
	
	@Override
	public Region getRegion() {
		return region;
	}
	
	@Override
	public Optional<Coordinates> checkRegion(World world, Region.Box region) {
		try {
			BiomeData data = world.getBiomeDataOracle().getBiomeData(region, true);
			
			return Optional.ofNullable(data.findFirst((x, y, b) -> {
				if(b != biome.getIndex())
					return null;
				
				Coordinates pos = region.getCorner().add(Coordinates.from(x, y, Resolution.QUARTER));
				if(checkDistance && !region.contains(pos))
					return null;
				return pos;
			}));
		} catch (MinecraftInterfaceException e) {
			AmidstLogger.error(e);
			return Optional.empty();
		}		
	}
	
	@Override
	public void addMarkers(WorldFilterResult.ResultItem item) {
		if(item.biome != null && item.biome != biome)
			throw new IllegalArgumentException("biome is already set!");
		
		item.biome = biome;
	}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof BiomeConstraint))
			return false;
		
		BiomeConstraint o = (BiomeConstraint) other;
		return region.equals(o.region)
			&& biome.equals(o.biome)
			&& checkDistance == o.checkDistance;
	}
	
	@Override
	public int hashCode() {
		int prime = 31;
		int h = region.hashCode();
		h = prime*h + biome.hashCode();
		h = prime*h + (checkDistance?0:1);
		return h;
	}
}
