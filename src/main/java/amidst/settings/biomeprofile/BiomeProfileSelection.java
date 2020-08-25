package amidst.settings.biomeprofile;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import amidst.documentation.ThreadSafe;
import amidst.logging.AmidstLogger;
import amidst.logging.AmidstMessageBox;
import amidst.mojangapi.world.biome.BiomeColor;
import amidst.mojangapi.world.biome.UnknownBiomeIdException;

@ThreadSafe
public class BiomeProfileSelection {
	private ConcurrentHashMap<Integer, BiomeColor> biomeColors;
	private Set<Integer> unknownBiomes;

	public BiomeProfileSelection(BiomeProfile biomeProfile) {
		set(biomeProfile);
	}

	public BiomeColor getBiomeColorOrUnknown(int index) {
		try {
			return getBiomeColor(index);
		} catch (UnknownBiomeIdException e) {
			// Only show an error if this is the first time we encounter this biome
			if (unknownBiomes.add(index)) {
				AmidstLogger.error(e);
				AmidstMessageBox.displayError("Error", e);
			}
			return BiomeColor.unknown();
		}
	}

	public BiomeColor getBiomeColor(int index) throws UnknownBiomeIdException {
		BiomeColor color = biomeColors.get(index);
		if(color != null) {
			return color;
		} else {
			throw new UnknownBiomeIdException("unsupported biome index detected: " + index);
		}
	}

	public void set(BiomeProfile biomeProfile) {
		this.biomeColors = biomeProfile.createBiomeColorMap();
		this.unknownBiomes = ConcurrentHashMap.newKeySet();
		AmidstLogger.info("Biome profile activated: " + biomeProfile.getName());
	}
}
