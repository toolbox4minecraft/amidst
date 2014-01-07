package amidst.map.layers;

import java.util.Random;

import amidst.Options;
import amidst.map.Fragment;
import amidst.map.ImageLayer;

public class SlimeLayer extends ImageLayer {
	private static int size = Fragment.SIZE >> 4;
	private Random random = new Random();
	public SlimeLayer() {
		super(size);
	}
	
	@Override
	public boolean isVisible() {
		return Options.instance.showSlimeChunks.get();
	}
	
	@Override
	public void drawToCache(Fragment fragment) {
		int[] dataCache = Fragment.getIntArray();
		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {
				int xPosition = fragment.getChunkX() + x;
				int yPosition = fragment.getChunkY() + y;
				random.setSeed(Options.instance.seed +
					xPosition * xPosition * 0x4c1906 + 
					xPosition * 0x5ac0db + 
					yPosition * yPosition * 0x4307a7L + 
					yPosition * 0x5f24f ^ 0x3ad8025f);
				
				dataCache[y * size + x] = (random.nextInt(10) == 0) ? 0xA0FF00FF : 0x00000000;
			}
		}
		
		fragment.setImageData(layerId, dataCache);
	}
	
}
