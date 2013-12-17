package amidst.map.layers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.Random;

import amidst.Options;
import amidst.Util;
import amidst.map.Fragment;
import amidst.map.Layer;

public class SlimeLayer extends Layer {
	private static int size = Fragment.SIZE >> 4;
	private Random random = new Random();
	public SlimeLayer() {
		super("slime", null, 0.0f, size);
		setVisibilityPref(Options.instance.showSlimeChunks);
	}
	
	public void drawToCache(Fragment fragment, int layerID) {
		int[] dataCache = Fragment.getIntArray();
		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {
				int xPosition = fragment.getChunkX() + x;
				int yPosition = fragment.getChunkY() + y;
				random.setSeed(Options.instance.seed +
					(long) (xPosition * xPosition * 0x4c1906) + 
					(long) (xPosition * 0x5ac0db) + 
					(long) (yPosition * yPosition) * 0x4307a7L + 
					(long) (yPosition * 0x5f24f) ^ 0x3ad8025f);
				
				dataCache[y * size + x] = (random.nextInt(10) == 0) ? 0xA0FF00FF : 0x00000000;
			}
		}
		
		fragment.setImageData(layerID, dataCache);
	}
	
}
