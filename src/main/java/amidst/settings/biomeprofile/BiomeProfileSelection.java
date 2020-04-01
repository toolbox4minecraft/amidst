package amidst.settings.biomeprofile;

import amidst.documentation.ThreadSafe;
import amidst.logging.AmidstLogger;
import amidst.logging.AmidstMessageBox;
import amidst.mojangapi.world.biome.BiomeColor;
import amidst.mojangapi.world.biome.UnknownBiomeIdException;

@ThreadSafe
public class BiomeProfileSelection {
	private volatile BiomeColor[] biomeColors;

	public BiomeProfileSelection(BiomeProfile biomeProfile) {
		set(biomeProfile);
	}

	public BiomeColor getBiomeColorOrUnknown(int index) {
		try {
			return getBiomeColor(index);
		} catch (UnknownBiomeIdException e) {
			AmidstLogger.error(e);
			AmidstMessageBox.displayError("Error", e);
			return BiomeColor.unknown();
		}
	}

	public BiomeColor getBiomeColor(int index) throws UnknownBiomeIdException {
		BiomeColor[] biomeColors = this.biomeColors;
		if (index < 0 || index >= biomeColors.length || biomeColors[index] == null) {
			throw new UnknownBiomeIdException("unsupported biome index detected: " + index);
		} else {
			return biomeColors[index];
		}
	}

	public void set(BiomeProfile biomeProfile) {
		this.biomeColors = biomeProfile.createBiomeColorArray();
		AmidstLogger.info("Biome profile activated: " + biomeProfile.getName());
	}
}
