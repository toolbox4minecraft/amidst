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


@Immutable
public class StructureConstraint implements Constraint {

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
