package amidst.map.layers;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import amidst.Options;
import amidst.map.Fragment;
import amidst.map.IconLayer;
import amidst.map.MapObjectTemple;
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
				if (checkChunk(chunkX, chunkY)) {
					//getValidTemple(frag, x << 4, y << 4);
					String biomeName = BiomeLayer.getBiomeNameForFragment(frag, x << 4, y << 4);
					if (biomeName.equals("Swampland"))
						frag.addObject(new MapObjectWitchHut(x << 4, y << 4).setParent(this));
					else
						frag.addObject(new MapObjectTemple(x << 4, y << 4).setParent(this));
				}
			}
		}
	}
	
	public List<Biome> getValidBiomes() {
		Biome[] validBiomes;
		
		if (MinecraftUtil.getVersion().isAtLeast(VersionInfo.V1_4_2)) {
			validBiomes = new Biome[] {
				Biome.desert,
				Biome.desertHills,
				Biome.jungle,
				Biome.jungleHills,
				Biome.swampland
			};
		} else if (MinecraftUtil.getVersion().isAtLeast(VersionInfo.V12w22a)) {
			validBiomes = new Biome[] {
				Biome.desert,
				Biome.desertHills,
				Biome.jungle
			};
		} else {
			validBiomes = new Biome[] {
				Biome.desert,
				Biome.desertHills
			};
		}
		
		return Arrays.asList(validBiomes);
	}

	public boolean checkChunk(int chunkX, int chunkY) {
		int i = 32;
		int j = 8;
		
		int k = chunkX;
		int m = chunkY;
		if (chunkX < 0) chunkX -= i - 1;
		if (chunkY < 0) chunkY -= i - 1;
		
		int n = chunkX / i;
		int i1 = chunkY / i;
		long l1 = n * 341873128712L + i1 * 132897987541L + Options.instance.seed + 14357617;
		random.setSeed(l1);
		n *= i;
		i1 *= i;
		n += random.nextInt(i - j);
		i1 += random.nextInt(i - j);
		
		return (k == n) && (m == i1) && MinecraftUtil.isValidBiome(k * 16 + 8, m * 16 + 8, 0, validBiomes);
	}
}
