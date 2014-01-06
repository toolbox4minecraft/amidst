package amidst.map.layers;

import amidst.Util;
import amidst.map.Fragment;
import amidst.map.ImageLayer;
import amidst.minecraft.Biome;

public class BiomeFilterLayer extends ImageLayer {

	private static int size = Fragment.SIZE >> 2;
	
	public BiomeFilterLayer() {
		super(size);
	}

	
	@Override
	public void drawToCache(Fragment fragment, int layerID) {
		int[] dataCache = Fragment.getIntArray();
		
		// TODO: Cache these values?
		for (int i = 0; i < size*size; i++) {
			if (fragment.biomeData[i] == 2)
				dataCache[i] = 0;
			else
				dataCache[i] = Util.greyScale(Biome.biomes[fragment.biomeData[i]].color);
		}
		fragment.setImageData(layerID, dataCache);
	}
	
	@Override
	public float getAlpha() {
		 float val = (float) Math.sin(System.currentTimeMillis() % 3141592/200.0f) + 0.5f;
		 if (val > 1.0f) val = 1.0f;
		 if (val < 0.0f) val = 0.0f;
		 return val;
	}
}
