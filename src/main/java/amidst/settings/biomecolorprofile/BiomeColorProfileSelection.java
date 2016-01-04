package amidst.settings.biomecolorprofile;

import amidst.documentation.ThreadSafe;
import amidst.logging.Log;
import amidst.mojangapi.world.biome.BiomeColor;
import amidst.mojangapi.world.biome.UnknownBiomeIndexException;

@ThreadSafe
public class BiomeColorProfileSelection {
	private volatile BiomeColor[] biomeColors;

	public BiomeColorProfileSelection(BiomeColorProfile biomeColorProfile) {
		set(biomeColorProfile);
	}

	public BiomeColor getBiomeColorOrUnknown(int index) {
		try {
			return getBiomeColor(index);
		} catch (UnknownBiomeIndexException e) {
			Log.e(e.getMessage());
			e.printStackTrace();
			return BiomeColor.unknown();
		}
	}

	public BiomeColor getBiomeColor(int index)
			throws UnknownBiomeIndexException {
		BiomeColor[] biomeColors = this.biomeColors;
		if (index < 0 || index >= biomeColors.length
				|| biomeColors[index] == null) {
			throw new UnknownBiomeIndexException(
					"unsupported biome index detected: " + index);
		} else {
			return biomeColors[index];
		}
	}

	public void set(BiomeColorProfile biomeColorProfile) {
		this.biomeColors = biomeColorProfile.createBiomeColorArray();
		Log.i("Biome color profile activated.");
	}
}
