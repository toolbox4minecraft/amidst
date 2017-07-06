package amidst.filter;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import amidst.documentation.Immutable;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.coordinates.Coordinates;
import amidst.mojangapi.world.coordinates.Region;
import amidst.mojangapi.world.icon.WorldIcon;
import amidst.mojangapi.world.icon.producer.WorldIconProducer;
import amidst.mojangapi.world.icon.type.DefaultWorldIconTypes;


//TODO more efficient, maybe with a cache ?
@Immutable
public class StructureConstraint implements Constraint {
	
	// @formatter:off
	public static final Set<DefaultWorldIconTypes> UNSUPPORTED_STRUCTURES =
		Collections.unmodifiableSet(new HashSet<>(Arrays.asList(new DefaultWorldIconTypes[]{
			DefaultWorldIconTypes.PLAYER,
			DefaultWorldIconTypes.SPAWN,
			DefaultWorldIconTypes.END_CITY,
			DefaultWorldIconTypes.POSSIBLE_END_CITY
		})));
	// @formatter:on
		
	private final Region region;
	private final DefaultWorldIconTypes structure;
	private final Biome biome;
	private final boolean checkDistance;
	
	public StructureConstraint(Region region, DefaultWorldIconTypes structure, Biome biome, boolean check) {
		this.region = region;
		this.structure = structure;
		this.biome = biome;
		checkDistance = check;
	}
	
	@Override
	public Region getRegion() {
		return region;
	}
	
	@Override
	public Optional<Coordinates> checkRegion(World world, Region.Box region) {
		WorldIconProducer<Void> producer = getStructureProducer(world, structure);
		
		for(WorldIcon icon: producer.getAt(region, null)) {
			if(!icon.getName().equals(structure.getLabel()))
				continue;

			Coordinates pos = icon.getCoordinates();
			if(checkDistance && !region.contains(pos))
				continue;
			
			if(biome == null)
				return Optional.of(pos);
				
			try {
				short bid = world.getBiomeDataOracle().getBiomeAt(pos.getX(), pos.getY());
				if(bid == biome.getIndex())
					return Optional.of(pos);
			} catch (MinecraftInterfaceException e) {
				AmidstLogger.error(e);
			}
		}
		
		return Optional.empty();
	}
	
	@Override
	public void addMarkers(WorldFilterResult.ResultItem item) {
		if(item.biome != null) {
			if(item.biome != biome)
				throw new IllegalArgumentException("biome is already set!");
			item.biome = biome;
		}
		
		else item.icons.add(structure);		
	}
	
	private static WorldIconProducer<Void> getStructureProducer(World world, DefaultWorldIconTypes struct) {
		switch(struct) {
		case MINESHAFT:
			return world.getMineshaftProducer();
			
		case NETHER_FORTRESS:
			return world.getNetherFortressProducer();
			
		case OCEAN_MONUMENT:
			return world.getOceanMonumentProducer();
			
		case STRONGHOLD:
			return world.getStrongholdProducer();
			
		case VILLAGE:
			return world.getVillageProducer();

		case DESERT:
		case IGLOO:
		case JUNGLE:
		case WITCH:
			return world.getTempleProducer();

		case END_CITY:
		case PLAYER:
		case POSSIBLE_END_CITY:
		case SPAWN:
			break;
		}
		
		throw new IllegalArgumentException("unsupported structure " + struct.getName());
	}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof StructureConstraint))
			return false;
		
		StructureConstraint o = (StructureConstraint) other;
		return region.equals(o.region)
			&& biome.equals(o.biome)
			&& checkDistance == o.checkDistance
			&& structure == o.structure;
	}
	
	@Override
	public int hashCode() {
		int prime = 31;
		int h = region.hashCode();
		h = prime*h + biome.hashCode();
		h = prime*h + (checkDistance?0:1);
		h = prime*h + structure.hashCode();
		return h;
	}
}
