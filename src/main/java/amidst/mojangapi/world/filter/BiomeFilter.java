package amidst.mojangapi.world.filter;

import java.lang.IllegalArgumentException;
import java.util.ArrayList;
import java.util.List;

import amidst.mojangapi.world.World;
import amidst.mojangapi.world.biome.Biome;

public class BiomeFilter extends BaseFilter {
	private final List<Biome> biomes;

	public BiomeFilter(long worldFilterSize, List<String> biomeNames, String group, long scoreValue) {
		super(worldFilterSize);

		this.scoreValue = scoreValue;
		this.group = group;

		List<Biome> biomes = new ArrayList<>();
		for (String name : biomeNames) {
			Biome biome = Biome.getByName(name);
			if (biome == null) {
				List<String> possibleNames = new ArrayList<String>();
				for (Biome possibleBiome : Biome.allBiomes()) {
					possibleNames.add(possibleBiome.getName());
				}
				throw new IllegalArgumentException(
						"Biome name '" + name + "' should be one of: " + String.join(", ", possibleNames));
			}
			biomes.add(biome);
		}

		this.biomes = biomes;
	}

	@Override
	protected boolean isValid(World world, short[][] region) {
		// since the passed area can be larger than this filter
		// determine the offset to only search the applicable area
		long dataOffset = region.length - this.quarterFilterSize;

		// loop over only the area specified by the filter
		for (long x = dataOffset; x < dataOffset + this.quarterFilterSize; x++) {
			for (long y = dataOffset; y < dataOffset + this.quarterFilterSize; y++) {
				if (isValidBiome(region[(int) x][(int) y])) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean isValidBiome(short biomeIndex) {
		for (Biome biome : biomes) {
			if (biomeIndex == biome.getIndex()) {
				return true;
			}
		}
		return false;
	}
}