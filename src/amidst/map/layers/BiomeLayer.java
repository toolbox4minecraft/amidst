package amidst.map.layers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import amidst.Log;
import amidst.Util;
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
		
		int x = fragment.getChunkX() << 2;
		int y = fragment.getChunkY() << 2;
		
		int[] biomeData = MinecraftUtil.getBiomeData(x, y, size, size);
		for (int i = 0; i < size*size; i++)
			if (Biome.biomes[biomeData[i]] != null)
				dataCache[i] = Biome.biomes[biomeData[i]].color;
			else
				Log.debug("Failed to find biome ID: " + biomeData[i]); // TODO: This could turn into spam
		fragment.setImageData(layerID, dataCache);
	}
	// TODO: This shouldn't be static, it should use the ID provided when it's loaded in for getBufferedImage
	public static int getBiomeForFragment(Fragment frag, int blockX, int blockY) {
		int pixel = frag.getBufferedImage(0).getRGB(blockX >> 2, blockY >> 2);
		for (int i = 0; i < Biome.length; i++) {
			if (pixel == Biome.biomes[i].color)
				return i;
		}
		for (int i = 128; i < Biome.length + 128; i++) {
			if (pixel == Biome.biomes[i].color)
				return i;
		}
		return 0;
	}
	
	public static String getBiomeNameForFragment(Fragment frag, int blockX, int blockY) {
		return Biome.biomes[getBiomeForFragment(frag, blockX, blockY)].name;
	}
	
	
}
