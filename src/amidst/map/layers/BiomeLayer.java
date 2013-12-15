package amidst.map.layers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import amidst.Options;
import amidst.Util;
import amidst.logging.Log;
import amidst.map.ByteArrayCache;
import amidst.map.CacheManager;
import amidst.map.Fragment;
import amidst.map.Layer;
import amidst.minecraft.Biome;
import amidst.minecraft.MinecraftUtil;

public class BiomeLayer extends Layer {

	private static int size = Fragment.SIZE >> 2;
	public BiomeLayer() {
		super("biome", null, 0.0f, size);
		isTransparent = false;
	}
	
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
		return (int)frag.biomeData[(blockY >> 2) * Fragment.BIOME_SIZE + (blockX >> 2)];
	}
	
	public static String getBiomeNameForFragment(Fragment frag, int blockX, int blockY) {
		return Biome.biomes[getBiomeForFragment(frag, blockX, blockY)].name;
	}
	public static String getBiomeAliasForFragment(Fragment frag, int blockX, int blockY) {
		return Options.instance.biomeColorProfile.getAliasForId(getBiomeForFragment(frag, blockX, blockY));
	}
	
	
}
