package amidst.mojangapi.world.icon.producer;

import java.util.List;

import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.mojangapi.world.icon.locationchecker.StructureBiomeLocationChecker;
import amidst.mojangapi.world.icon.type.DefaultWorldIconTypes;
import amidst.mojangapi.world.icon.type.ImmutableWorldIconTypeProvider;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

public class WoodlandMansionProducer extends RegionalStructureProducer<Void> {
	private static final Resolution RESOLUTION = Resolution.CHUNK;
	private static final int OFFSET_IN_WORLD = 8;
	private static final Dimension DIMENSION = Dimension.OVERWORLD;
	private static final boolean DISPLAY_DIMENSION = false;
	
	private static final long SALT = 10387319L;
	private static final byte SPACING = 80;
	private static final byte SEPARATION = 20;
	private static final boolean IS_TRIANGULAR = true;
	
	private static final int STRUCTURE_SIZE = 32;
	
	public WoodlandMansionProducer(
			long worldSeed,
			BiomeDataOracle biomeDataOracle,
			List<Biome> validBiomesForStructure) {
		
		super(RESOLUTION,
			  OFFSET_IN_WORLD,
			  new StructureBiomeLocationChecker(biomeDataOracle, STRUCTURE_SIZE, validBiomesForStructure),
			  new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.WOODLAND_MANSION),
			  DIMENSION,
			  DISPLAY_DIMENSION,
			  worldSeed,
			  SALT,
			  SPACING,
			  SEPARATION,
			  IS_TRIANGULAR
			 );
	}
	
}
