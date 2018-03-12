package amidst.settings.biomeprofile;

import amidst.fragment.layer.LayerIds;
import amidst.fragment.layer.LayerManager;
import amidst.gameengineabstraction.GameEngineDetails;
import amidst.gameengineabstraction.world.biome.IBiome;
import amidst.gui.main.viewer.BiomeSelection;
import amidst.mojangapi.world.biome.UnknownBiomeIndexException;

/**
 * An attempt to prevent all the biome stuff turning into spaghetti
 * as Minetest needs to access the classes from places where Minecraft 
 * code could just access static singletons.
 */
public class BiomeAuthority implements BiomeProfileUpdateListener {

	private BiomeProfileDirectory biomeProfileDirectory;
	private BiomeProfileSelection biomeProfileSelection;
	private BiomeSelection biomeSelection;
	/**
	 * Can be null
	 */
	private LayerManager layerManager;
	
	public BiomeAuthority(String root, BiomeProfileSelection inject_biomeProfileSelection) {
		
		// Produces BiomeProfiles
		biomeProfileDirectory = BiomeProfileDirectory.create(root);
		
		// Consumes BiomeProfiles
		if (inject_biomeProfileSelection == null) {
			biomeProfileSelection = new BiomeProfileSelection(BiomeProfileImpl.getDefaultProfile());
		} else {
			biomeProfileSelection = inject_biomeProfileSelection;
			biomeProfileSelection.addUpdateListener(this);
		}
		
		// Records GUI-selected state of biome indexes
		biomeSelection = new BiomeSelection();
	}
	
	public void onBiomeProfileUpdate(BiomeProfile newBiomeProfile) {
		if (layerManager != null && newBiomeProfile != null) {
			// Only Minetest needs to recalculate biomes when the BiomeProfile changes
			if (newBiomeProfile.invalidatesBiomeData()) {
				layerManager.invalidateLayer(LayerIds.BIOME_DATA);
			}
		}
	}	
	
	/**
	 * Setting this allows the biome data to be recalculated when Minetest's biome profile is changed.
	 */
	public void setLayerManager(LayerManager layerManager) {
		this.layerManager = layerManager;
	}
	
	public BiomeProfileDirectory getBiomeProfileDirectory() {
		return biomeProfileDirectory;
	}
	
	public BiomeProfileSelection getBiomeProfileSelection() {
		return biomeProfileSelection;
	}
	
	public BiomeSelection getBiomeSelection() {
		return biomeSelection;
	}
	
	public Iterable<IBiome> getAllBiomes() {
		return biomeProfileSelection.getCurrentBiomeProfile().allBiomes();
	}
	
	public IBiome getBiomeByIndex(int index) throws UnknownBiomeIndexException {
		return biomeProfileSelection.getCurrentBiomeProfile().getByIndex(index);
	}
		
	/** Updates the biomes to match the specified engine */
	public void selectGameEngine(GameEngineDetails currentGameEngine) {
		biomeProfileDirectory.selectGameEngine(
			currentGameEngine.getType().getAbbreviatedName().toLowerCase(),
			currentGameEngine.getBiomeProfileImplementation()
		);
		
		// TODO: Makes sure biomeProfileSelection has a profile from the reconfigured biomeProfileDirectory
		//       Then refresh biomeSelection
	};
}
