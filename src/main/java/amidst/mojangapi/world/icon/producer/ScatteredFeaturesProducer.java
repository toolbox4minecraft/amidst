package amidst.mojangapi.world.icon.producer;

import java.util.List;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.mojangapi.world.icon.locationchecker.BiomeLocationChecker;
import amidst.mojangapi.world.icon.type.WorldIconTypeProvider;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

@ThreadSafe
public class ScatteredFeaturesProducer extends RegionalStructureProducer<Void> {
	private static final byte DEFAULT_SPACING = 32;
	private static final byte DEFAULT_SEPARATION = 8;
	private static final boolean IS_TRIANGULAR = false;

	public ScatteredFeaturesProducer(
			Resolution resolution,
			int offsetInWorld,
			BiomeDataOracle biomeDataOracle,
			List<Biome> validBiomesAtMiddleOfChunk,
			WorldIconTypeProvider<Void> provider,
			Dimension dimension,
			boolean displayDimension,
			long worldSeed,
			long salt,
			boolean buggyStructureCoordinateMath) {

		this(resolution,
			 offsetInWorld,
			 biomeDataOracle,
			 validBiomesAtMiddleOfChunk,
			 provider,
			 dimension,
			 displayDimension,
			 worldSeed,
			 salt,
			 DEFAULT_SPACING,
			 DEFAULT_SEPARATION,
			 buggyStructureCoordinateMath
			);
	}

	public ScatteredFeaturesProducer(
			Resolution resolution,
			int offsetInWorld,
			BiomeDataOracle biomeDataOracle,
			List<Biome> validBiomesAtMiddleOfChunk,
			WorldIconTypeProvider<Void> provider,
			Dimension dimension,
			boolean displayDimension,
			long worldSeed,
			long salt,
			byte spacing,
			byte separation,
			boolean buggyStructureCoordinateMath) {
		
		super(resolution,
			  offsetInWorld,
			  validBiomesAtMiddleOfChunk == null ? null : new BiomeLocationChecker(biomeDataOracle, validBiomesAtMiddleOfChunk),
			  provider,
			  dimension,
			  displayDimension,
			  worldSeed,
			  salt,
			  spacing,
			  separation,
			  IS_TRIANGULAR,
			  buggyStructureCoordinateMath
			);
	}
	
}
