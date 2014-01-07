package amidst.map.layers;

import amidst.Util;
import amidst.map.Fragment;
import amidst.map.ImageLayer;
import amidst.minecraft.Biome;

public class BiomeFilterLayer extends ImageLayer {
	public BiomeFilterLayer instance;
	private static int size = Fragment.SIZE >> 2;
	
	public BiomeFilterLayer() {
		super(size);
		instance = this;
	}

	
	@Override
	public void drawToCache(Fragment fragment) {
		int[] dataCache = Fragment.getIntArray();
		for (int i = 0; i < size*size; i++) {
			if (fragment.biomeData[i] == 2)
				dataCache[i] = 0xFF000000 | (Biome.biomes[fragment.biomeData[i]].color * 2);
			else
				dataCache[i] = Util.greyScale(Biome.biomes[fragment.biomeData[i]].color);
		}
		fragment.setImageData(layerId, dataCache);
	}
	
	@Override
	public float getAlpha() {
		return alpha;
	}
	
	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}
}
