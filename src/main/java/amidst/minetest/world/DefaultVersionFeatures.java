package amidst.minetest.world;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import amidst.documentation.Immutable;
import amidst.fragment.IBiomeDataOracle;
import amidst.fragment.layer.LayerIds;
import amidst.gameengineabstraction.world.WorldTypes;
import amidst.gameengineabstraction.world.versionfeatures.IVersionFeatures;
import amidst.gameengineabstraction.world.versionfeatures.VersionFeaturesFactory;
import amidst.logging.AmidstLogger;
import amidst.minetest.world.mapgen.MapgenParams;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.WorldType;
import amidst.mojangapi.world.versionfeatures.TriFunction;
import amidst.mojangapi.world.versionfeatures.VersionFeature;
import amidst.settings.biomeprofile.BiomeProfileSelection;

@Immutable
public class DefaultVersionFeatures implements VersionFeaturesFactory {

	private final Map<WorldType, VersionFeature<List<Integer>>> enabledLayers;
	private final WorldTypes worldTypes;
	private final VersionFeature<Map<WorldType, TriFunction<Long, MapgenParams, BiomeProfileSelection, IBiomeDataOracle>>> biomeDataOracle;
	
	@Override
	public IVersionFeatures create(RecognisedVersion version) {		
		return create(null, version);
	}

	@Override
	public IVersionFeatures create(WorldType worldType, RecognisedVersion version) {
		
		if (!enabledLayers.containsKey(worldType)) {
			if (worldType != null) AmidstLogger.error("DefaultVersionFeatures asked for unknown worldtype: " + worldType);
			// Default to a worldtype that is known			
			worldType = WorldType.V7; // V7 exposes all layers
		}
		
		return new MinetestVersionFeatures(			
				enabledLayers.get(worldType).getValue(version),
				worldTypes, 
				biomeDataOracle.getValue(version));
	}
	

	public DefaultVersionFeatures() {
		// @formatter:off
		
		this.worldTypes = new WorldTypes(
				new WorldType[]{
						WorldType.V7, 
						WorldType.V7_FLOATLANDS, 
						WorldType.V6, 
						WorldType.V5, 
						WorldType.CARPATHIAN,
						WorldType.FLAT, 
						WorldType.HALLELUJAH_MOUNTAINS, 
						//WorldType.FRACTAL
				}
		);
		
		List<Integer> commonLayers = Arrays.asList(new Integer[] {
				LayerIds.ALPHA,
				LayerIds.BIOME_DATA,
				LayerIds.BACKGROUND,
				LayerIds.GRID
		});
		
		enabledLayers = new HashMap<WorldType, VersionFeature<List<Integer>>>();
		enabledLayers.put(WorldType.HALLELUJAH_MOUNTAINS,
				VersionFeature.<Integer> listBuilder()
				.init(commonLayers)
				.initExtend(
						LayerIds.MINETEST_OCEAN
						//,LayerIds.MINETEST_MOUNTAIN
				).construct()
		);
		enabledLayers.put(WorldType.CARPATHIAN,
				VersionFeature.<Integer> listBuilder()
				.init(commonLayers)
				.initExtend(
						LayerIds.MINETEST_OCEAN,
						LayerIds.MINETEST_MOUNTAIN
				).construct()
		);
		enabledLayers.put(WorldType.V7,
				VersionFeature.<Integer> listBuilder()
				.init(commonLayers)
				.initExtend(
						LayerIds.MINETEST_OCEAN,
						LayerIds.MINETEST_RIVER,
						LayerIds.MINETEST_MOUNTAIN
				).construct()
		);
		enabledLayers.put(WorldType.V7_FLOATLANDS,
				VersionFeature.<Integer> listBuilder()
				.init(commonLayers)
				.initExtend(
						LayerIds.MINETEST_OCEAN,
						LayerIds.MINETEST_MOUNTAIN
				).construct()
		);
		enabledLayers.put(WorldType.V6,
				VersionFeature.<Integer> listBuilder()
				.init(commonLayers)
				.initExtend(
						LayerIds.MINETEST_OCEAN,
						LayerIds.MINETEST_MOUNTAIN
				).construct()
		);
		enabledLayers.put(WorldType.V5,
				VersionFeature.<Integer> listBuilder()
				.init(commonLayers)
				.initExtend(
						LayerIds.MINETEST_OCEAN
				).construct()
		);
		enabledLayers.put(WorldType.FLAT,
				VersionFeature.<Integer> listBuilder()
				.init(commonLayers)
				.construct()
		);
		enabledLayers.put(WorldType.FRACTAL,
				VersionFeature.<Integer> listBuilder()
				.init(commonLayers)
				.construct()
		);
				
		// This stuff will be hard to wrap your head around, just follow the pattern.
		// It had to be this way because of how VersionFeatures for Minecraft are handled
		this.biomeDataOracle = VersionFeature.<WorldType, TriFunction<Long, MapgenParams, BiomeProfileSelection, IBiomeDataOracle>> mapBuilder()
				.init(
						new AbstractMap.SimpleEntry<WorldType, TriFunction<Long, MapgenParams, BiomeProfileSelection, IBiomeDataOracle>>(
								WorldType.HALLELUJAH_MOUNTAINS,
								(seed, mapgenParams, biomeProfile) -> new amidst.minetest.world.oracle.BiomeDataOracleHallelujah(mapgenParams, biomeProfile, seed)
						),
						new AbstractMap.SimpleEntry<WorldType, TriFunction<Long, MapgenParams, BiomeProfileSelection, IBiomeDataOracle>>(
								WorldType.V7,
								(seed, mapgenParams, biomeProfile) -> new amidst.minetest.world.oracle.BiomeDataOracleV7(false, mapgenParams, biomeProfile, seed)
						),
						new AbstractMap.SimpleEntry<WorldType, TriFunction<Long, MapgenParams, BiomeProfileSelection, IBiomeDataOracle>>(
								WorldType.V7_FLOATLANDS,
								(seed, mapgenParams, biomeProfile) -> new amidst.minetest.world.oracle.BiomeDataOracleV7(true, mapgenParams, biomeProfile, seed)
						),
						new AbstractMap.SimpleEntry<WorldType, TriFunction<Long, MapgenParams, BiomeProfileSelection, IBiomeDataOracle>>(
								WorldType.V6,
								(seed, mapgenParams, biomeProfile) -> new amidst.minetest.world.oracle.BiomeDataOracleV6(mapgenParams, biomeProfile, seed)
						),
						new AbstractMap.SimpleEntry<WorldType, TriFunction<Long, MapgenParams, BiomeProfileSelection, IBiomeDataOracle>>(
								WorldType.V5, 
								(seed, mapgenParams, biomeProfile) -> new amidst.minetest.world.oracle.BiomeDataOracleV5(mapgenParams, biomeProfile, seed)
						),
						new AbstractMap.SimpleEntry<WorldType, TriFunction<Long, MapgenParams, BiomeProfileSelection, IBiomeDataOracle>>(
								WorldType.CARPATHIAN, 
								(seed, mapgenParams, biomeProfile) -> new amidst.minetest.world.oracle.BiomeDataOracleCarpathian(mapgenParams, biomeProfile, seed)
						),
						new AbstractMap.SimpleEntry<WorldType, TriFunction<Long, MapgenParams, BiomeProfileSelection, IBiomeDataOracle>>(
								WorldType.FLAT, 
								(seed, mapgenParams, biomeProfile) -> new amidst.minetest.world.oracle.BiomeDataOracleFlat(mapgenParams, biomeProfile, seed)
						)
				).construct();
		
	}
}
