package amidst.minetest.world;

import java.util.List;

import amidst.documentation.Immutable;
import amidst.fragment.layer.LayerIds;
import amidst.gameengineabstraction.world.WorldTypes;
import amidst.gameengineabstraction.world.versionfeatures.IVersionFeatures;
import amidst.gameengineabstraction.world.versionfeatures.VersionFeaturesFactory;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.WorldType;
import amidst.mojangapi.world.versionfeatures.VersionFeature;
import amidst.mojangapi.world.versionfeatures.VersionFeatures;

@Immutable
public class DefaultVersionFeatures implements VersionFeaturesFactory {

	private final VersionFeature<List<Integer>> enabledLayers_Minetest;
	private final WorldTypes worldTypes;
	
	@Override
	public IVersionFeatures create(RecognisedVersion version) {
		
		// Using minecraft's implementation of IVersionFeatures for now,
		// for no other reason than to reduce refactoring at the moment.
		return new VersionFeatures(			
				enabledLayers_Minetest.getValue(version),
				worldTypes,
				null, 
				null, 
				(seed, biomeOracle, validBiomes) -> null, 
				null, 
				null, 
				seed -> null, 
				(seed, biomeOracle, validCenterBiomes, validBiomes) -> null,
				null, 
				null); 			
	}


	public DefaultVersionFeatures() {
		// @formatter:off

		this.worldTypes = new WorldTypes(
				new WorldType[]{
						//WorldType.V5, 
						WorldType.V6, 
						WorldType.V7, 
						//WorldType.FLAT, 
						//WorldType.FRACTAL
				}
		);
		
		this.enabledLayers_Minetest = VersionFeature.<Integer> listBuilder()
				.init(
						LayerIds.ALPHA,
						LayerIds.BIOME_DATA,
						LayerIds.BACKGROUND,
						LayerIds.GRID,
						LayerIds.MINETEST_OCEAN,
						LayerIds.MINETEST_RIVER,
						LayerIds.MINETEST_MOUNTAIN
				).construct();
	}
}
