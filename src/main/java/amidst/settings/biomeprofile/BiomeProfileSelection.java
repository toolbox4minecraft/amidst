package amidst.settings.biomeprofile;

import java.util.ArrayList;

import amidst.documentation.ThreadSafe;
import amidst.logging.AmidstLogger;
import amidst.logging.AmidstMessageBox;
import amidst.minetest.world.mapgen.MinetestBiome;
import amidst.mojangapi.world.biome.BiomeColor;
import amidst.mojangapi.world.biome.UnknownBiomeIndexException;

@ThreadSafe
public class BiomeProfileSelection {
	private volatile BiomeColor[] biomeColors;
	private volatile BiomeProfile biomeProfile;
	private ArrayList<BiomeProfileUpdateListener> listeners = new ArrayList<BiomeProfileUpdateListener>();

	public BiomeProfileSelection(BiomeProfile biomeProfile) {
		set(biomeProfile);
	}

	public BiomeColor getBiomeColorOrUnknown(int index) {
		try {
			return getBiomeColor(index);
		} catch (UnknownBiomeIndexException e) {
			if (index >= 0) {
				// less then zero indicates Unknown/NONE, which biome oracles are allowed to return
				AmidstLogger.error(e);
				AmidstMessageBox.displayError("Error", e);
			}
			return BiomeColor.unknown();
		}
	}

	/**
	 * (Private because you probably want getBiomeColorOrUnknown())
	 */
	private BiomeColor getBiomeColor(int index) throws UnknownBiomeIndexException {
		BiomeColor[] biomeColors = this.biomeColors;
		if (index < 0 || index >= biomeColors.length || biomeColors[index] == null) {
			throw new UnknownBiomeIndexException("unsupported biome index detected: " + index);
		} else {
			return biomeColors[index];
		}
	}
	
	public void addUpdateListener(BiomeProfileUpdateListener listener) {
		listeners.add(listener);
	}
	public void removeUpdateListener(BiomeProfileUpdateListener listener) {
		listeners.remove(listener);
	}	
	
	/**
	 * Used by BiomeAuthority to be able to provide getAllBiomes() and getBiomeByIndex()
	 */
	public BiomeProfile getCurrentBiomeProfile() {
		return biomeProfile;
	}

	public void set(BiomeProfile biomeProfile) {

		this.biomeProfile = biomeProfile;

		// Because the number of biomes in Minetest is not fixed, and so the new biomeProfile
		// might contain fewer biomes than are currently being displayed by fragments 
		// (until their biome data is recalculated), we don't ever reduce the size of the
		// of the biomeColors array - just leave any old out-of-range values where they are. 
		// This saves worrying about race conditions between updating the biomeColors and 
		// recalculating the fragment biome maps.
		BiomeColor[] newColorArray = biomeProfile.createBiomeColorArray();
		if (this.biomeColors != null && newColorArray.length < this.biomeColors.length) {
			for(int i = 0; i < newColorArray.length; i++) this.biomeColors[i] = newColorArray[i];
		} else {
			this.biomeColors = newColorArray;	
		}
		
		AmidstLogger.info("Biome profile activated: " + biomeProfile.getName());
		
		for (BiomeProfileUpdateListener listener: listeners) {
			listener.onBiomeProfileUpdate(this.biomeProfile);
		}		
	}
}
