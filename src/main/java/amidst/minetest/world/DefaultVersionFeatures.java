package amidst.minetest.world;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

import amidst.documentation.Immutable;
import amidst.fragment.IBiomeDataOracle;
import amidst.fragment.layer.LayerIds;
import amidst.gameengineabstraction.world.WorldTypes;
import amidst.gameengineabstraction.world.versionfeatures.IVersionFeatures;
import amidst.gameengineabstraction.world.versionfeatures.VersionFeaturesFactory;
import amidst.minetest.world.mapgen.MapgenParams;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.WorldType;
import amidst.mojangapi.world.versionfeatures.TriFunction;
import amidst.mojangapi.world.versionfeatures.VersionFeature;
import amidst.settings.biomeprofile.BiomeProfileSelection;

@Immutable
public class DefaultVersionFeatures implements VersionFeaturesFactory {

	private final VersionFeature<List<Integer>> enabledLayers;
	private final WorldTypes worldTypes;
	private final VersionFeature<Map<WorldType, TriFunction<Long, MapgenParams, BiomeProfileSelection, IBiomeDataOracle>>> biomeDataOracle;
	
	@Override
	public IVersionFeatures create(RecognisedVersion version) {
		
		return new MinetestVersionFeatures(			
				enabledLayers.getValue(version),
				worldTypes, 
				biomeDataOracle.getValue(version));
	}


	public DefaultVersionFeatures() {
		// @formatter:off

		this.worldTypes = new WorldTypes(
				new WorldType[]{
						//WorldType.CARPATHIAN,
						WorldType.V7, 
						WorldType.V6, 
						WorldType.V5, 
						WorldType.FLAT, 
						//WorldType.FRACTAL
				}
		);
		
		this.enabledLayers = VersionFeature.<Integer> listBuilder()
				.init(
						LayerIds.ALPHA,
						LayerIds.BIOME_DATA,
						LayerIds.BACKGROUND,
						LayerIds.GRID,
						LayerIds.MINETEST_OCEAN,
						LayerIds.MINETEST_RIVER,
						LayerIds.MINETEST_MOUNTAIN,
						LayerIds.MINETEST_DUNGEON
				).construct();
		
		// This stuff will be hard to wrap your head around, just follow the pattern.
		// It had to be this way because of how VersionFeatures for Minecraft are handled
		this.biomeDataOracle = VersionFeature.<WorldType, TriFunction<Long, MapgenParams, BiomeProfileSelection, IBiomeDataOracle>> mapBuilder()
				.init(
						new AbstractMap.SimpleEntry<WorldType, TriFunction<Long, MapgenParams, BiomeProfileSelection, IBiomeDataOracle>>(
								WorldType.V7,
								(seed, mapgenParams, biomeProfile) -> new amidst.minetest.world.oracle.BiomeDataOracleV7(mapgenParams, biomeProfile, seed)
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
								WorldType.FLAT, 
								(seed, mapgenParams, biomeProfile) -> new amidst.minetest.world.oracle.BiomeDataOracleFlat(mapgenParams, biomeProfile, seed)
						)
				).construct();
		
	}
}
