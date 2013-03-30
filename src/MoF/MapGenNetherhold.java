package MoF;

import java.util.Random;

public class MapGenNetherhold
{
  private Random c;
  private long seed;
  public MapGenNetherhold(long seed)
  {
	  c = new Random();
	  this.seed = seed;
  }

  protected boolean checkChunk(int paramInt1, int paramInt2)
  {
    int i = paramInt1 >> 4;
    int j = paramInt2 >> 4;

    this.c.setSeed((long)(i ^ j << 4) ^ seed);
    this.c.nextInt();

    if (this.c.nextInt(3) != 0) {
      return false;
    }
    if (paramInt1 != (i << 4) + 4 + this.c.nextInt(8)) {
      return false;
    }

    return paramInt2 == (j << 4) + 4 + this.c.nextInt(8);
  }
}
