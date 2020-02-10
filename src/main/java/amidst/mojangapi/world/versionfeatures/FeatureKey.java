package amidst.mojangapi.world.versionfeatures;

import java.util.List;
import java.util.function.Function;

import amidst.documentation.Immutable;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.icon.locationchecker.LocationChecker;
import amidst.mojangapi.world.icon.producer.CachedWorldIconProducer;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

@Immutable
public class FeatureKey<T> {

	// @formatter:off
	public static final FeatureKey<List<Integer>> ENABLED_LAYERS                                  = make();
	public static final FeatureKey<List<Biome>>   VALID_BIOMES_FOR_STRUCTURE_SPAWN                = make();
	public static final FeatureKey<List<Biome>>   VALID_BIOMES_AT_MIDDLE_OF_CHUNK_STRONGHOLD      = make();
	public static final FeatureKey<TriFunction<Long, BiomeDataOracle, List<Biome>, CachedWorldIconProducer>>
                                                  STRONGHOLD_PRODUCER_FACTORY                     = make();
	public static final FeatureKey<List<Biome>>   VALID_BIOMES_FOR_STRUCTURE_VILLAGE              = make();
	public static final FeatureKey<List<Biome>>   VALID_BIOMES_FOR_STRUCTURE_PILLAGER_OUTPOST     = make();
	public static final FeatureKey<Boolean>       DO_COMPLEX_VILLAGE_CHECK                        = make();
	public static final FeatureKey<Integer>       OUTPOST_VILLAGE_AVOID_DISTANCE                  = make();
	public static final FeatureKey<List<Biome>>   VALID_BIOMES_AT_MIDDLE_OF_CHUNK_DESERT_TEMPLE   = make();
	public static final FeatureKey<List<Biome>>   VALID_BIOMES_AT_MIDDLE_OF_CHUNK_IGLOO           = make();
	public static final FeatureKey<List<Biome>>   VALID_BIOMES_AT_MIDDLE_OF_CHUNK_JUNGLE_TEMPLE   = make();
	public static final FeatureKey<List<Biome>>   VALID_BIOMES_AT_MIDDLE_OF_CHUNK_WITCH_HUT       = make();
	public static final FeatureKey<List<Biome>>   VALID_BIOMES_AT_MIDDLE_OF_CHUNK_OCEAN_RUINS     = make();
	public static final FeatureKey<List<Biome>>   VALID_BIOMES_AT_MIDDLE_OF_CHUNK_SHIPWRECK       = make();
	public static final FeatureKey<Function<Long, LocationChecker>>
                                                  MINESHAFT_ALGORITHM_FACTORY                     = make();
	public static final FeatureKey<QuadFunction<Long, BiomeDataOracle, List<Biome>, List<Biome>, LocationChecker>>
                                                  OCEAN_MONUMENT_LOCATION_CHECKER_FACTORY         = make();
	public static final FeatureKey<List<Biome>>   VALID_BIOMES_AT_MIDDLE_OF_CHUNK_OCEAN_MONUMENT  = make();
	public static final FeatureKey<List<Biome>>   VALID_BIOMES_AT_MIDDLE_OF_CHUNK_BURIED_TREASURE = make();
	public static final FeatureKey<List<Biome>>   VALID_BIOMES_FOR_STRUCTURE_OCEAN_MONUMENT       = make();
	public static final FeatureKey<List<Biome>>   VALID_BIOMES_FOR_STRUCTURE_WOODLAND_MANSION     = make();
	public static final FeatureKey<Long>          SEED_FOR_STRUCTURE_DESERT_TEMPLE                = make();
	public static final FeatureKey<Long>          SEED_FOR_STRUCTURE_IGLOO                        = make();
	public static final FeatureKey<Long>          SEED_FOR_STRUCTURE_JUNGLE_TEMPLE                = make();
	public static final FeatureKey<Long>          SEED_FOR_STRUCTURE_WITCH_HUT                    = make();
	public static final FeatureKey<Long>          SEED_FOR_STRUCTURE_OCEAN_RUINS                  = make();
	public static final FeatureKey<Long>          SEED_FOR_STRUCTURE_SHIPWRECK                    = make();
	public static final FeatureKey<Long>          SEED_FOR_STRUCTURE_BURIED_TREASURE              = make();
	public static final FeatureKey<Byte>          MAX_DISTANCE_SCATTERED_FEATURES_SHIPWRECK       = make();
	public static final FeatureKey<Byte>          MIN_DISTANCE_SCATTERED_FEATURES_SHIPWRECK       = make();
	public static final FeatureKey<Byte>          MAX_DISTANCE_SCATTERED_FEATURES_OCEAN_RUINS     = make();
	public static final FeatureKey<Boolean>       BUGGY_STRUCTURE_COORDINATE_MATH                 = make();
	// @formatter:on

	private FeatureKey() {
	}

	public static<T> FeatureKey<T> make() {
		return new FeatureKey<>();
	}
}
