package amidst.mojangapi.world.filter;

import java.lang.IllegalArgumentException;
import java.util.ArrayList;
import java.util.List;

import amidst.mojangapi.world.World;
import amidst.mojangapi.world.biome.Biome;

public class BiomeFilter extends BaseFilter {
	private final List<Biome> biomes;

	public BiomeFilter(long worldFilterSize, List<String> biomeNames) {
		super(worldFilterSize);

		List<Biome> biomes = new ArrayList<>();
		for (String name : biomeNames) {
			Biome biome = Biome.getByName(name);
			if (biome == null) {
				List<String> possibleNames = new ArrayList<String>();
				for (Biome possibleBiome : Biome.allBiomes()) {
					possibleNames.add(possibleBiome.getName());
				}
				throw new IllegalArgumentException("Biome name '" + name + 
						"' should be one of: " + String.join(", ", possibleNames));
			}
			biomes.add(biome);
		}

		this.biomes = biomes;
	}

	@Override
	protected boolean isValid(World world, short[][] region) {
		for (short[] row : region) {
			for (short entry : row) {
				if (isValidBiome(entry)) {
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