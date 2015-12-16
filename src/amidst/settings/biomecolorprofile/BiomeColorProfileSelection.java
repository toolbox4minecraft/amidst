package amidst.settings.biomecolorprofile;

import amidst.documentation.ThreadSafe;
import amidst.logging.Log;
import amidst.mojangapi.world.BiomeColor;

@ThreadSafe
public class BiomeColorProfileSelection {
	private volatile BiomeColor[] biomeColors;

	public BiomeColorProfileSelection(BiomeColorProfile biomeColorProfile) {
		set(biomeColorProfile);
	}

	public BiomeColor getBiomeColor(int index) {
		return biomeColors[index];
	}

	public void set(BiomeColorProfile biomeColorProfile) {
		this.biomeColors = biomeColorProfile.createBiomeColorArray();
		Log.i("Biome color profile activated.");
	}
}
