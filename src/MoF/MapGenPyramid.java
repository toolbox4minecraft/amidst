package MoF;

import amidst.foreign.VersionInfo;

import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class MapGenPyramid {
	public boolean checkChunk(int paramInt1, int paramInt2, long seed, ChunkManager x) {
		int i = 32;
		int j = 8;
		
		int k = paramInt1;
		int m = paramInt2;
		if (paramInt1 < 0) paramInt1 -= i - 1;
		if (paramInt2 < 0) paramInt2 -= i - 1;
		
		int n = paramInt1 / i;
		int i1 = paramInt2 / i;
		Random localRandom = new Random();
	    long l1 = n * 341873128712L + i1 * 132897987541L + seed + 14357617;
	    localRandom.setSeed(l1);
		n *= i;
		i1 *= i;
		n += localRandom.nextInt(i - j);
		i1 += localRandom.nextInt(i - j);
		
		return (k == n) && (m == i1)
				&& x.a(k * 16 + 8, m * 16 + 8, 0, templeBiomes());
	}
	
	public List<Biome> templeBiomes() {
		Biome[] ret;
		
		if (ReflectionInfo.instance.version.isAtLeast(VersionInfo.V1_4_2))
			ret = new Biome[] { Biome.d, Biome.s, Biome.w, Biome.x, Biome.h };
		else if (ReflectionInfo.instance.version.isAtLeast(VersionInfo.V12w22a))
			ret = new Biome[] { Biome.d, Biome.s, Biome.w };
		else
			ret = new Biome[] { Biome.d, Biome.s };
		
		return Arrays.asList(ret);
	}
}
