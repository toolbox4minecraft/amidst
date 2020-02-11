package amidst.mojangapi.world.versionfeatures;

import java.util.List;

import amidst.documentation.Immutable;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.icon.locationchecker.LocationChecker;
import amidst.mojangapi.world.icon.producer.CachedWorldIconProducer;

@Immutable
public class FeatureKey<T> {

	// @formatter:off
	public static final FeatureKey<List<Integer>>                  ENABLED_LAYERS                            = make();
	public static final FeatureKey<List<Biome>>                    VALID_BIOMES_FOR_STRUCTURE_SPAWN          = make();
	public static final FeatureKey<WorldFunction<LocationChecker>> NETHER_FORTRESS_LOCATION_CHECKER_FACTORY  = make();
	public static final FeatureKey<WorldFunction<LocationChecker>> END_ISLAND_LOCATION_CHECKER_FACTORY       = make();
	public static final FeatureKey<WorldFunction<LocationChecker>> MINESHAFT_LOCATION_CHECKER_FACTORY        = make();
	public static final FeatureKey<WorldFunction<CachedWorldIconProducer>> STRONGHOLD_PRODUCER_FACTORY       = make();
	public static final FeatureKey<WorldFunction<LocationChecker>> VILLAGE_LOCATION_CHECKER_FACTORY          = make();
	public static final FeatureKey<WorldFunction<LocationChecker>> PILLAGER_OUTPOST_LOCATION_CHECKER_FACTORY = make();
	public static final FeatureKey<WorldFunction<LocationChecker>> DESERT_TEMPLE_LOCATION_CHECKER_FACTORY    = make();
	public static final FeatureKey<WorldFunction<LocationChecker>> IGLOO_LOCATION_CHECKER_FACTORY            = make();
	public static final FeatureKey<WorldFunction<LocationChecker>> JUNGLE_TEMPLE_LOCATION_CHECKER_FACTORY    = make();
	public static final FeatureKey<WorldFunction<LocationChecker>> WITCH_HUT_LOCATION_CHECKER_FACTORY        = make();
	public static final FeatureKey<WorldFunction<LocationChecker>> OCEAN_MONUMENT_LOCATION_CHECKER_FACTORY   = make();
	public static final FeatureKey<WorldFunction<LocationChecker>> WOODLAND_MANSION_LOCATION_CHECKER_FACTORY = make();
	public static final FeatureKey<WorldFunction<LocationChecker>> OCEAN_RUINS_LOCATION_CHECKER_FACTORY      = make();
	public static final FeatureKey<WorldFunction<LocationChecker>> SHIPWRECK_LOCATION_CHECKER_FACTORY        = make();
	public static final FeatureKey<WorldFunction<LocationChecker>> BURIED_TREASURE_LOCATION_CHECKER_FACTORY  = make();
	// @formatter:on

	private FeatureKey() {
	}

	public static<T> FeatureKey<T> make() {
		return new FeatureKey<>();
	}
}
