package amidst.map.layers;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import MoF.Biome;
import MoF.ChunkManager;
import amidst.Log;
import amidst.Options;
import amidst.map.Fragment;
import amidst.map.IconLayer;
import amidst.map.MapObjectNether;
import amidst.map.MapObjectVillage;

public class VillageLayer extends IconLayer {
	public static List<Biome> a = Arrays.asList(new Biome[] { Biome.c, Biome.d });
	
	public VillageLayer() {
		super("villages");
		setVisibilityPref(Options.instance.showIcons);
	}
	public void generateMapObjects(Fragment frag) {
		int size = Fragment.SIZE >> 4;
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				int chunkX = x + frag.getChunkX();
				int chunkY = y + frag.getChunkY();
				if (checkChunk(chunkX, chunkY, chunkManager)) {
					frag.addObject(new MapObjectVillage(x << 4, y << 4).setParent(this));
				}
			}
		}
	}
	 

	public boolean checkChunk(int chunkX, int chunkY, ChunkManager chunkManager) {
		// TODO: Confirm this code is the same for 1.6.2+
		byte i = 32;
		byte j = 8;
		
		int k = chunkX;
		int m = chunkY;
		if (chunkX < 0) chunkX -= i - 1;
		if (chunkY < 0) chunkY -= i - 1;
		
		int n = chunkX / i;
		int i1 = chunkY / i;
		
		Random localRandom = new Random();
		long positionSeed = n * 341873128712L + i1 * 132897987541L + chunkManager.seed + 10387312L;
		localRandom.setSeed(positionSeed);
		
		
		
		n *= i;
		i1 *= i;
		n += localRandom.nextInt(i - j);
		i1 += localRandom.nextInt(i - j);
		chunkX = k;
		chunkY = m;
		if ((chunkX == n) && (chunkY == i1))
			return chunkManager.a(chunkX * 16 + 8, chunkY * 16 + 8, 0, a);
		
		return false;
	}
}
