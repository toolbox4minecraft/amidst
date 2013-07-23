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

public class StrongholdLayer extends IconLayer {
	public static List<Biome> a = Arrays.asList(new Biome[] { Biome.c, Biome.d });
	
	public StrongholdLayer() {
		super("strongholds");
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
		for (int i = 0; i < 3; i++) {
			//if (chunkManager.strongholds[i].)
			
		}
		return false;
	}
}
