package amidst.minetest.world;

import java.util.List;
import java.util.Map;

import amidst.fragment.IBiomeDataOracle;
import amidst.gameengineabstraction.world.WorldTypes;
import amidst.gameengineabstraction.world.versionfeatures.IVersionFeatures;
import amidst.minetest.world.mapgen.MapgenParams;
import amidst.mojangapi.world.WorldType;
import amidst.mojangapi.world.versionfeatures.TriFunction;
import amidst.settings.biomeprofile.BiomeProfileSelection;

public class MinetestVersionFeatures implements IVersionFeatures {	
	private final List<Integer> enabledLayers;
	private final WorldTypes worldTypes;
	private final Map<WorldType, TriFunction<Long, MapgenParams, BiomeProfileSelection, IBiomeDataOracle>> factories_BiomeDataOracle;

	public MinetestVersionFeatures(
			List<Integer> enabledLayers,
			WorldTypes worldTypes,
			Map<WorldType, TriFunction<Long, MapgenParams, BiomeProfileSelection, IBiomeDataOracle>> factories_BiomeDataOracle) {
		this.enabledLayers = enabledLayers;
		this.worldTypes= worldTypes;
		this.factories_BiomeDataOracle = factories_BiomeDataOracle;
	}

	@Override
	public WorldTypes getWorldTypes() {
		return worldTypes;
	}
	
	@Override
	public boolean hasLayer(int layerId) {
		return enabledLayers.contains(layerId);
	}
	
	public TriFunction<Long, MapgenParams, BiomeProfileSelection, IBiomeDataOracle> getFactory_BiomeDataOracle(WorldType worldType) {
		return factories_BiomeDataOracle.get(worldType);
	}	
}
