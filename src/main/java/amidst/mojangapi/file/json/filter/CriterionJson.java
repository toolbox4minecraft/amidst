package amidst.mojangapi.file.json.filter;

import java.util.List;

import amidst.documentation.GsonConstructor;
import amidst.logging.Log;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Region;
import amidst.mojangapi.world.filter.ConstraintBiome;
import amidst.mojangapi.world.filter.Criterion;
import amidst.mojangapi.world.filter.CriterionAnd;
import amidst.mojangapi.world.filter.CriterionNegate;
import amidst.mojangapi.world.filter.CriterionOr;
import amidst.mojangapi.world.filter.CriterionSimple;

import java.util.ArrayList;
import java.util.Collections;

public class CriterionJson {
	
	private volatile String type;
	private volatile boolean negate;
	
	private volatile List<CriterionJson> children = Collections.emptyList();
	
	private volatile CoordinatesInWorld center;
	private volatile long radius;
	private volatile boolean square;
	
	private volatile List<String> biomes;
	private transient List<Biome> biomeList;
	
	@GsonConstructor
	public CriterionJson() {
	}
	
	public Criterion getCriterion(World world) {
		Criterion criterion = null;
		switch(type) {
		case "and":
			criterion = getAndCriterion(world);
			break;
			
		case "or":
			criterion = getOrCriterion(world);
			break;
			
		case "biome":
			criterion = getBiomeCriterion(world);
			break;
			
		default:
			throw new IllegalStateException("illegal criterion type: " + type);
		}
		
		if(negate)
			criterion = new CriterionNegate(criterion);
		return criterion;
		
	}
	
	private Criterion getAndCriterion(World world) {
		if(children.isEmpty())
			throw new IllegalStateException("criterion 'and' must have at least one children");
		return new CriterionAnd(children, c -> c.getCriterion(world));
	}
	
	private Criterion getOrCriterion(World world) {
		if(children.isEmpty())
			throw new IllegalStateException("criterion 'or' must have at least one children");
		return new CriterionOr(children, c -> c.getCriterion(world));
	}
	
	private Criterion getBiomeCriterion(World world) {
		Region region = getRegion(world);
		
		if(biomeList == null) {
			if(biomes.isEmpty())
				throw new IllegalStateException("criterion 'biome' must have at least one biome");
			
			biomeList = new ArrayList<>(biomes.size());
			
			for(String name: biomes) {
				Biome biome = Biome.getByName(name);
				if(biome == null)
					throw new IllegalStateException("unknown biome name '" + name + "'");
				biomeList.add(biome);
			}
		}
		
		if(biomeList.size() == 1)
			return new CriterionSimple(new ConstraintBiome(region, biomeList.get(0)));

		return new CriterionOr(biomeList, biome -> new CriterionSimple(new ConstraintBiome(region, biome)));
	}
	
	private Region getRegion(World world) {
		if(radius < 0)
			throw new IllegalStateException("radius must be positive");
		
		CoordinatesInWorld c = center == null ? world.getSpawnOracle().get() : center;
		if(c == null) {
			c = CoordinatesInWorld.origin();
			Log.i("Unable to find spawn biome. Falling back to " + c + ".");
		}
		
		if(square) {
			return Region.box(c.getX() - radius, c.getY() - radius, 2*radius, 2*radius);
		}
		
		return Region.circle(c, radius);
	}	
}
