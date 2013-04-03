package MoF;

import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class MapGenPyramid {
	private static List<Biome> a = Arrays.asList(new Biome[] { Biome.d, Biome.s});
	private static List<Biome> b = Arrays.asList(new Biome[] { Biome.d, Biome.s, Biome.w});
	private static List<Biome> c = Arrays.asList(new Biome[] {Biome.d, Biome.s, Biome.w, Biome.x, Biome.h});
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
		paramInt1 = k;
		paramInt2 = m;
		
		if ((paramInt1 == n) && (paramInt2 == i1)) {
			boolean bool = false;
			if (ReflectionInfo.versionID >= 50) //1.4.2
				bool = x.a(paramInt1 * 16 + 8, paramInt2 * 16 + 8, 0, c);
			else if (ReflectionInfo.versionID >= 23) //12w22a
				bool = x.a(paramInt1 * 16 + 8, paramInt2 * 16 + 8, 0, b);
			else
				bool = x.a(paramInt1 * 16 + 8, paramInt2 * 16 + 8, 0, a);
			if (bool) {
				return true;
			}
		}
		
		return false;
	}
}
