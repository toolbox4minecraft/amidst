package amidst.map.layers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import amidst.Util;
import amidst.map.ByteArrayCache;
import amidst.map.CacheManager;
import amidst.map.Fragment;
import amidst.map.Layer;

public class BiomeLayer extends Layer {
	public static int[] biomeColors = new int[] {
		Util.makeColor(13 ,51 ,219), //Ocean;
		Util.makeColor(104,222,104), //Plains;
		Util.makeColor(226,242,131), //Desert;
		
		Util.makeColor(171,105,34 ), //Extreme Hills;
		Util.makeColor(40 ,181,22 ), //Forest;
		Util.makeColor(32 ,110,22 ), //Taiga;
		
		Util.makeColor(108,158,79), //Swampland;
		Util.makeColor(14,127,227), //River;
		
		Util.makeColor(143,25 ,10 ), //Hell;
		Util.makeColor(209,233,235), //Sky;
		
		Util.makeColor(70 ,104,199), //FrozenOcean;
		Util.makeColor(171,216,255), //FrozenRiver;
		Util.makeColor(156,214,190), //Ice Plains;
		Util.makeColor(151,162,130), //Ice Mountains;
		
		Util.makeColor(219,196,164), //MushroomIsland;
		Util.makeColor(242,216,179), //MushroomIslandShore;
		
		Util.makeColor(255,254,189), //Beach
		Util.makeColor(230,202, 78), //DesertHills
		Util.makeColor(89 ,176, 32), //ForestHills
		Util.makeColor(66 ,110, 22), //TaigaHills
		Util.makeColor(186,159, 39),  //Extreme Hills Edge
		
		Util.makeColor(26 ,87 ,34 ),
		Util.makeColor(73 ,105,33 )
	};
	public static String[] biomeNames = new String[] {
		"Ocean",
		"Plains",
		"Desert",
		
		"Extreme Hills",
		"Forest",
		"Taiga",
		
		"Swampland",
		"River",
		
		"Hell",
		"Sky",
		
		"Frozen Ocean",
		"Frozen River",
		"Ice Plains",
		"Ice Mountains",
		
		"Mushroom Island",
		"Mushroom Island Shore",
		
		"Beach",
		"Desert Hills",
		"Forest Hills",
		"Taiga Hills",
		"Extreme Hills Edge",
		
		"Jungle",
		"Jungle Hills"
	};
	
	private static int size = Fragment.SIZE >> 2;
	
	public BiomeLayer() {
		super("biome", null, 0.0f, size);
		setLive(false);
	}
	public void drawCached(Fragment fragment, int layerID) {
		int[] dataCache = Fragment.getIntArray();
		
		int x = fragment.getChunkX() << 2;
		int y = fragment.getChunkY() << 2;
		
		// TODO : Change this when ChunkManager is removed!
		int[] biomeData = chunkManager.ba(x, y, size, size);
		dataCache = Util.arrayToColors(biomeData, dataCache, biomeColors, size*size);
		fragment.setImageData(layerID, dataCache);
	}
	// TODO: This shouldn't be static, it should use the ID provided when it's loaded in for getBufferedImage
	public static int getBiomeForFragment(Fragment frag, int blockX, int blockY) {
		int pixel = frag.getBufferedImage(0).getRGB(blockX >> 2, blockY >> 2);
		for (int i = 0; i < BiomeLayer.biomeColors.length; i++) {
			if (pixel == BiomeLayer.biomeColors[i])
				return i;
		}
		return 0;
	}
	
	public static String getBiomeNameForFragment(Fragment frag, int blockX, int blockY) {
		return biomeNames[getBiomeForFragment(frag, blockX, blockY)];
	}
	
	
}
