package amidst.mojangapi.world.versionfeatures;

import java.util.List;
import java.util.Optional;

import amidst.documentation.Immutable;
import amidst.mojangapi.world.WorldOptions;
import amidst.mojangapi.world.biome.BiomeList;
import amidst.mojangapi.world.icon.producer.CachedWorldIconProducer;
import amidst.mojangapi.world.icon.producer.VillageProducer;
import amidst.mojangapi.world.icon.producer.WorldIconProducer;
import amidst.mojangapi.world.oracle.BiomeDataOracle;
import amidst.mojangapi.world.oracle.EndIsland;
import amidst.mojangapi.world.oracle.EndIslandOracle;
import amidst.mojangapi.world.oracle.SlimeChunkOracle;
import amidst.mojangapi.world.oracle.WorldSpawnOracle;

@Immutable
public class FeatureKey<T> {

	// @formatter:off
	public static final FeatureKey<WorldOptions>     WORLD_OPTIONS                        = make();
	public static final FeatureKey<BiomeDataOracle>  OVERWORLD_BIOME_DATA_ORACLE          = make();
	public static final FeatureKey<Optional<BiomeDataOracle>> NETHER_BIOME_DATA_ORACLE    = make();
	public static final FeatureKey<BiomeList>        BIOME_LIST                           = make();

	public static final FeatureKey<List<Integer>>    ENABLED_LAYERS                       = make();
	public static final FeatureKey<SlimeChunkOracle> SLIME_CHUNK_ORACLE                   = make();
	public static final FeatureKey<EndIslandOracle>  END_ISLAND_ORACLE                    = make();
	public static final FeatureKey<WorldSpawnOracle> WORLD_SPAWN_ORACLE                   = make();
	public static final FeatureKey<WorldIconProducer<?>> NETHER_FORTRESS_PRODUCER         = make();
	public static final FeatureKey<WorldIconProducer<?>> BASTION_REMNANT_PRODUCER         = make();
	public static final FeatureKey<WorldIconProducer<List<EndIsland>>> END_CITY_PRODUCER  = make();
	public static final FeatureKey<WorldIconProducer<?>> MINESHAFT_PRODUCER               = make();
	public static final FeatureKey<CachedWorldIconProducer> STRONGHOLD_PRODUCER           = make();
	public static final FeatureKey<VillageProducer>         VILLAGE_PRODUCER              = make();
	public static final FeatureKey<WorldIconProducer<?>> PILLAGER_OUTPOST_PRODUCER        = make();
	public static final FeatureKey<WorldIconProducer<?>> DESERT_TEMPLE_PRODUCER           = make();
	public static final FeatureKey<WorldIconProducer<?>> IGLOO_PRODUCER                   = make();
	public static final FeatureKey<WorldIconProducer<?>> JUNGLE_TEMPLE_PRODUCER           = make();
	public static final FeatureKey<WorldIconProducer<?>> WITCH_HUT_PRODUCER               = make();
	public static final FeatureKey<WorldIconProducer<?>> OCEAN_MONUMENT_PRODUCER          = make();
	public static final FeatureKey<WorldIconProducer<?>> WOODLAND_MANSION_PRODUCER        = make();
	public static final FeatureKey<WorldIconProducer<?>> OCEAN_RUINS_PRODUCER             = make();
	public static final FeatureKey<WorldIconProducer<?>> SHIPWRECK_PRODUCER               = make();
	public static final FeatureKey<WorldIconProducer<?>> BURIED_TREASURE_PRODUCER         = make();
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
