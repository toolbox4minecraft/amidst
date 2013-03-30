package MoF;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MapGenVillage
{
  public static List<Biome> a = Arrays.asList(new Biome[] { Biome.c, Biome.d });

  public boolean checkChunk(int paramInt1, int paramInt2, long seed, ChunkManager x) {
    byte i = 32;
    byte j = 8;

    int k = paramInt1;
    int m = paramInt2;
    if (paramInt1 < 0) paramInt1 -= i - 1;
    if (paramInt2 < 0) paramInt2 -= i - 1;

    int n = paramInt1 / i;
    int i1 = paramInt2 / i;
    
    Random localRandom = new Random();
    long rannum = n * 341873128712L + i1 * 132897987541L + seed + 10387312L;
    localRandom.setSeed(rannum);
    
    
    
    n *= i;
    i1 *= i;
    n += localRandom.nextInt(i - j);
    i1 += localRandom.nextInt(i - j);
    paramInt1 = k;
    paramInt2 = m;
    if ((paramInt1 == n) && (paramInt2 == i1)) {
      boolean bool = x.a(paramInt1 * 16 + 8, paramInt2 * 16 + 8, 0, a);
      if (bool)
        return true;
    }

    return false;
  }
}