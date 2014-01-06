package amidst.map.layers;

import amidst.Options;
import amidst.logging.Log;
import amidst.map.Fragment;
import amidst.map.ImageLayer;
import amidst.minecraft.Biome;

public class BiomeLayer extends ImageLayer {
	private static int size = Fragment.SIZE >> 2;
	public BiomeLayer() {
		super(size);
	}
	
	@Override
	public void drawToCache(Fragment fragment, int layerID) {
		int[] dataCache = Fragment.getIntArray();
		
		for (int i = 0; i < size*size; i++)
			if (Biome.biomes[fragment.biomeData[i]] != null)
				dataCache[i] = Biome.biomes[fragment.biomeData[i]].color;
			else
				Log.debug("Failed to find biome ID: " + fragment.biomeData[i]); // TODO: This could turn into spam
		
		fragment.setImageData(layerID, dataCache);
	}
	
	public static int getBiomeForFragment(Fragment frag, int blockX, int blockY) {
		return frag.biomeData[(blockY >> 2) * Fragment.BIOME_SIZE + (blockX >> 2)];
	}
	
	public static String getBiomeNameForFragment(Fragment frag, int blockX, int blockY) {
		return Biome.biomes[getBiomeForFragment(frag, blockX, blockY)].name;
	}
	public static String getBiomeAliasForFragment(Fragment frag, int blockX, int blockY) {
		return Options.instance.biomeColorProfile.getAliasForId(getBiomeForFragment(frag, blockX, blockY));
	}
	
	
}
