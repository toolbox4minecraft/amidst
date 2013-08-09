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
	public static List<Biome> validBiomes = Arrays.asList(new Biome[] { Biome.c, Biome.d });
	
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
		byte villageParam1 = 32;
		byte villageParam2 = 8;
		
		int k = chunkX;
		int m = chunkY;
		if (chunkX < 0) chunkX -= villageParam1 - 1;
		if (chunkY < 0) chunkY -= villageParam1 - 1;
		
		int n = chunkX / villageParam1;
		int i1 = chunkY / villageParam1;
		
		Random localRandom = new Random();
		long positionSeed = n * 341873128712L + i1 * 132897987541L + chunkManager.seed + 10387312L;
		localRandom.setSeed(positionSeed);
		
		
		
		n *= villageParam1;
		i1 *= villageParam1;
		n += localRandom.nextInt(villageParam1 - villageParam2);
		i1 += localRandom.nextInt(villageParam1 - villageParam2);
		chunkX = k;
		chunkY = m;
		if ((chunkX == n) && (chunkY == i1))
			return chunkManager.a(chunkX * 16 + 8, chunkY * 16 + 8, 0, validBiomes);
		
		return false;
	}
}
