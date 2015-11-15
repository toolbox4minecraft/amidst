package amidst.map.layers;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import amidst.Options;
import amidst.logging.Log;
import amidst.map.Fragment;
import amidst.map.IconLayer;
import amidst.map.MapObjectDesertTemple;
import amidst.map.MapObjectJungleTemple;
import amidst.map.MapObjectWitchHut;
import amidst.minecraft.Biome;
import amidst.minecraft.MinecraftUtil;
import amidst.version.VersionInfo;

public class TempleLayer extends IconLayer {
	public static List<Biome> validBiomes;
	private Random random = new Random();

	public TempleLayer() {
		validBiomes = getValidBiomes();
	}

	@Override
	public boolean isVisible() {
		return Options.instance.showTemples.get();
	}

	@Override
	public void generateMapObjects(Fragment frag) {
		int size = Fragment.SIZE >> 4;
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				int chunkX = x + frag.getChunkX();
				int chunkY = y + frag.getChunkY();
				Biome chunkBiome = checkChunk(chunkX, chunkY);
				if (chunkBiome != null) {

					if (chunkBiome == Biome.swampland) {
						frag.addObject(new MapObjectWitchHut(x << 4, y << 4)
								.setParent(this));
					} else if (chunkBiome.name.contains("Jungle")) {
						frag.addObject(new MapObjectJungleTemple(x << 4, y << 4)
								.setParent(this));
					} else if (chunkBiome.name.contains("Desert")) {
						frag.addObject(new MapObjectDesertTemple(x << 4, y << 4)
								.setParent(this));
					} else {
						Log.e("No known structure for this biome type. checkChunk() may be faulting.");
					}
				}
			}
		}
	}

	public List<Biome> getValidBiomes() {
		Biome[] validBiomes;

		if (MinecraftUtil.getVersion().isAtLeast(VersionInfo.V1_4_2)) {
			validBiomes = new Biome[] { Biome.desert, Biome.desertHills,
					Biome.jungle, Biome.jungleHills, Biome.swampland };
		} else if (MinecraftUtil.getVersion().isAtLeast(VersionInfo.V12w22a)) {
			validBiomes = new Biome[] { Biome.desert, Biome.desertHills,
					Biome.jungle };
		} else {
			validBiomes = new Biome[] { Biome.desert, Biome.desertHills };
		}

		return Arrays.asList(validBiomes);
	}

	/**
	 * @return null if there is no structure in the chunk, otherwise returns the
	 *         biome (from validBiomes) that determines the type of structure.
	 */
	public Biome checkChunk(int chunkX, int chunkY) {

		Biome result = null;

		int maxDistanceBetweenScatteredFeatures = 32;
		int minDistanceBetweenScatteredFeatures = 8;

		int k = chunkX;
		int m = chunkY;
		if (chunkX < 0)
			chunkX -= maxDistanceBetweenScatteredFeatures - 1;
		if (chunkY < 0)
			chunkY -= maxDistanceBetweenScatteredFeatures - 1;

		int n = chunkX / maxDistanceBetweenScatteredFeatures;
		int i1 = chunkY / maxDistanceBetweenScatteredFeatures;
		long l1 = n * 341873128712L + i1 * 132897987541L
				+ Options.instance.seed + 14357617;
		random.setSeed(l1);
		n *= maxDistanceBetweenScatteredFeatures;
		i1 *= maxDistanceBetweenScatteredFeatures;
		n += random.nextInt(maxDistanceBetweenScatteredFeatures
				- minDistanceBetweenScatteredFeatures);
		i1 += random.nextInt(maxDistanceBetweenScatteredFeatures
				- minDistanceBetweenScatteredFeatures);

		if (k == n && m == i1) {
			// This is a potential feature biome

			// Since the structure-size that would be passed to
			// MinecraftUtil.isValidBiome()
			// is 0, we can use MinecraftUtil.getBiomeAt() here instead, which
			// tells us what kind of
			// structure it is.
			Biome chunkBiome = MinecraftUtil.getBiomeAt(k * 16 + 8, m * 16 + 8);
			if (validBiomes.contains(chunkBiome))
				result = chunkBiome;
		}
		return result;
	}
}
