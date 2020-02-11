package amidst.mojangapi.world.versionfeatures;

import java.util.List;

import amidst.documentation.Immutable;
import amidst.mojangapi.world.WorldOptions;
import amidst.mojangapi.world.icon.locationchecker.LocationChecker;
import amidst.mojangapi.world.icon.producer.CachedWorldIconProducer;
import amidst.mojangapi.world.oracle.BiomeDataOracle;
import amidst.mojangapi.world.oracle.EndIslandOracle;
import amidst.mojangapi.world.oracle.SlimeChunkOracle;
import amidst.mojangapi.world.oracle.WorldSpawnOracle;

@Immutable
public class FeatureKey<T> {

	// @formatter:off
	public static final FeatureKey<WorldOptions>     WORLD_OPTIONS                     = make();
	public static final FeatureKey<BiomeDataOracle>  BIOME_DATA_ORACLE                 = make();

	public static final FeatureKey<List<Integer>>    ENABLED_LAYERS                    = make();
	public static final FeatureKey<SlimeChunkOracle> SLIME_CHUNK_ORACLE                = make();
	public static final FeatureKey<EndIslandOracle>  END_ISLAND_ORACLE                 = make();
	public static final FeatureKey<WorldSpawnOracle>  WORLD_SPAWN_ORACLE               = make();
	public static final FeatureKey<LocationChecker>  NETHER_FORTRESS_LOCATION_CHECKER  = make();
	public static final FeatureKey<LocationChecker>  END_ISLAND_LOCATION_CHECKER       = make();
	public static final FeatureKey<LocationChecker>  MINESHAFT_LOCATION_CHECKER        = make();
	public static final FeatureKey<CachedWorldIconProducer> STRONGHOLD_PRODUCER        = make();
	public static final FeatureKey<LocationChecker>  VILLAGE_LOCATION_CHECKER          = make();
	public static final FeatureKey<LocationChecker>  PILLAGER_OUTPOST_LOCATION_CHECKER = make();
	public static final FeatureKey<LocationChecker>  DESERT_TEMPLE_LOCATION_CHECKER    = make();
	public static final FeatureKey<LocationChecker>  IGLOO_LOCATION_CHECKER            = make();
	public static final FeatureKey<LocationChecker>  JUNGLE_TEMPLE_LOCATION_CHECKER    = make();
	public static final FeatureKey<LocationChecker>  WITCH_HUT_LOCATION_CHECKER        = make();
	public static final FeatureKey<LocationChecker>  OCEAN_MONUMENT_LOCATION_CHECKER   = make();
	public static final FeatureKey<LocationChecker>  WOODLAND_MANSION_LOCATION_CHECKER = make();
	public static final FeatureKey<LocationChecker>  OCEAN_RUINS_LOCATION_CHECKER      = make();
	public static final FeatureKey<LocationChecker>  SHIPWRECK_LOCATION_CHECKER        = make();
	public static final FeatureKey<LocationChecker>  BURIED_TREASURE_LOCATION_CHECKER  = make();
	// @formatter:on


	private final StackTraceElement location;

	private FeatureKey(StackTraceElement location) {
		this.location = location;
	}

	public static<T> FeatureKey<T> make() {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		if (stackTrace.length < 2 || !stackTrace[2].getMethodName().equals("<clinit>")) {
			throw new IllegalStateException("make() must be called from a static initializer");
		}
		return new FeatureKey<>(stackTrace[2]);
	}

	@Override
	public String toString() {
		return "(" + location.getFileName() + ":" + location.getLineNumber() + ")";
	}
}
