package amidst.mojangapi.world.filter;

import java.util.Arrays;

import amidst.mojangapi.world.World;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Region;
import amidst.mojangapi.world.coordinates.Resolution;

public class ConstraintBiome implements Constraint {
	private Region region;
	private Biome biome;
	private boolean isSatisfied;
	
	public ConstraintBiome(Region region, Biome biome) {
		this.region = region;
		this.biome = biome;
	}
	
	public Region getRegion() {
		return region;
	}
	
	public Biome getBiome() {
		return biome;
	}
	
	/* Implementation detail (for performance's sake) :
	 * The function checkRegionForBiomes must be called with
	 *   1. correct data
	 *   2. a constraints list containing this
	 * for this method to return a correct value.
	 */
	@Override
	public boolean isSatisfied() {
		return isSatisfied;
	}

	/* This function checks all the biome constraints in one pass over a rectangular region.
	 * If a biome is found inside a constraint's regin, the constraint is marked as satisfied.
	 * The biomeData array must contains correct data. In particular, its size
	 * must be compatible with the region's size and the resolution.
	 */
	public static void checkRegionForBiomes(World world, Criterion criterion,
							Region.Box region, Resolution resolution, short[][] biomeData) {
		
		if(!initConstraintList(criterion, region))
			return;
		
		validateArraySize(region, resolution, biomeData);
		world.getBiomeDataOracle().populateArray(region.getCorner(), biomeData, resolution);
		
		int step = resolution.getStep();
		CoordinatesInWorld corner = region.getCorner().snapTo(resolution);
		long xInWorld = corner.getX();
		for(int i = 0; i < biomeData.length; i++) {
			long yInWorld = corner.getY();
			
			for(int j = 0; j < biomeData[i].length; j++) {
				processCoordinate(xInWorld, yInWorld, biomeData[i][j]);
				
				yInWorld += step;
			}
			
			xInWorld += step;
		}
	}
	
	//Variables used by checkRegionForBiomes
	//We use arrays here to limit the amount of pointer indirection.
	
	//ConstraintBiome[] storage for future reuses (avoid allocation).
	private static final ConstraintBiome[][] ARRAY_STORE = new ConstraintBiome[Biome.getBiomesLength()][4];
	
	//List of ConstraintBiome for each specific biome : can have nulls at the end of the array
	private static final ConstraintBiome[][] CONSTRAINTS = new ConstraintBiome[Biome.getBiomesLength()][];
	
	//The number of constraints for each biome
	private static final int[] CONSTRAINTS_LENGTH = new int[Biome.getBiomesLength()];
	
	//The total number of constraints
	private static int CONSTRAINTS_TOTAL_NUMBER = 0;
	

	private static void validateArraySize(Region region, Resolution resolution, short[][] biomeData) {
		if(biomeData.length * resolution.getStep() != region.getWidth()
		|| biomeData[0].length * resolution.getStep() != region.getHeight())
			throw new IllegalArgumentException("the array hasn't the correct size");
	}
	
	private static boolean initConstraintList(Criterion criterion, Region region) {
		Arrays.fill(CONSTRAINTS, null);
		Arrays.fill(CONSTRAINTS_LENGTH, 0);
		CONSTRAINTS_TOTAL_NUMBER = 0;
		
		criterion.forEachConstraint(constraint -> {
			if(!(constraint instanceof ConstraintBiome))
				return;
			ConstraintBiome c = (ConstraintBiome) constraint;
			
			if(!c.getRegion().intersectsWith(region))
				return;
			
			int b = c.getBiome().getIndex();
			
			if(CONSTRAINTS[b] == null) {
				CONSTRAINTS[b] = ARRAY_STORE[b];
				
			} else if(CONSTRAINTS_LENGTH[b] == ARRAY_STORE[b].length) {
				ARRAY_STORE[b] = new ConstraintBiome[CONSTRAINTS_LENGTH[b]*2];
				CONSTRAINTS[b] = ARRAY_STORE[b];
			}
			
			CONSTRAINTS[b][CONSTRAINTS_LENGTH[b]++] = c;
			CONSTRAINTS_TOTAL_NUMBER++;
		});
		
		return CONSTRAINTS_TOTAL_NUMBER > 0;
	}
	
	private static void removeConstraintAtIndex(int biome, int i) {
		if(CONSTRAINTS_LENGTH[biome] == 1) {
			CONSTRAINTS[biome][0] = null;
			CONSTRAINTS_LENGTH[biome] = 0;
		
		} else {
			ConstraintBiome[] arr = CONSTRAINTS[biome];
			arr[i] = arr[--CONSTRAINTS_LENGTH[biome]];
			arr[CONSTRAINTS_LENGTH[biome]] = null;
		}
	}
	
	private static void processCoordinate(long xInWorld, long yInWorld, int biome) {
		ConstraintBiome[] arr = CONSTRAINTS[biome];
		
		if(arr == null)
			return;
		
		for(int i = 0; i < arr.length; i++) {
			if(arr[i] == null)
				break;
			
			if(arr[i].getRegion().contains(xInWorld, yInWorld)) {
				arr[i].isSatisfied = true;
				removeConstraintAtIndex(biome, i);
				i--;
			}
		}
		
		if(CONSTRAINTS_LENGTH[biome] == 0)
			CONSTRAINTS[biome] = null;
	}

}
