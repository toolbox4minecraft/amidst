package MoF;
import amidst.Log;
import amidst.foreign.VersionInfo;
import amidst.map.MapObjectStronghold;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

public class MapGenStronghold {
	private Biome[] a  = { Biome.d, Biome.f, Biome.e, Biome.h };
	private Biome[] b  = { Biome.d, Biome.f, Biome.e, Biome.h, Biome.g, Biome.n, Biome.o };
	private Biome[] ab = { Biome.d, Biome.f, Biome.e, Biome.h, Biome.g, Biome.n, Biome.o, Biome.s, Biome.t, Biome.v };
	private Biome[] ac = { Biome.d, Biome.f, Biome.e, Biome.h, Biome.g, Biome.n, Biome.o, Biome.s, Biome.t, Biome.v, Biome.w, Biome.x };
	public MapObjectStronghold[] coords = new MapObjectStronghold[3];
	private Random c = new Random();
	public static boolean reset0 = false;
	protected MapObjectStronghold[] a(long seed, ChunkManager x) {
		int j;
		c = new Random();
		this.c.setSeed(seed);
		
		double d1 = this.c.nextDouble() * 3.141592653589793D * 2.0D;
		for (j = 0; j < this.coords.length; j++) {
			double d2 = (1.25D + this.c.nextDouble()) * 32.0D;
			Log.debug(d2);
			int k = (int)Math.round(Math.cos(d1) * d2);
			int m = (int)Math.round(Math.sin(d1) * d2);
			Biome[] v = a;
			if (ReflectionInfo.instance.version == VersionInfo.V1_9pre6 || ReflectionInfo.instance.version == VersionInfo.V1_0)
				v = b;
			if (ReflectionInfo.instance.version == VersionInfo.V1_1)
				v = ab;
			if (ReflectionInfo.instance.version.isAtLeast(VersionInfo.V12w03a))
				v = ac;
			ArrayList<Biome> localArrayList = new ArrayList<Biome>();
			for (Object localObject2 : v) {
				localArrayList.add((Biome) localObject2);
			}
			
			Point localPoint = x.a((k << 4) + 8, (m << 4) + 8, 112, localArrayList, this.c);
			if (localPoint != null) {
				k = localPoint.x >> 4;
				m = localPoint.y >> 4;
			}
			coords[j] = new MapObjectStronghold((k << 4), (m << 4));
			
			d1 += 6.283185307179586D / this.coords.length;
		}
		if (reset0)
			coords[0] = new MapObjectStronghold(0, 0);
		
		return coords;
	}
}