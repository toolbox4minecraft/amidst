package amidst.map.layers;

import java.util.Random;

import amidst.Util;
import amidst.map.Fragment;
import amidst.map.ImageLayer;
import amidst.minecraft.Biome;

public class BiomeFilterLayer extends ImageLayer {
	public BiomeFilterLayer instance;
	private static int size = Fragment.SIZE >> 2;
	private boolean isFadingIn = false;
	private int highlightId = 2;
	
	public BiomeFilterLayer() {
		super(size);
		fillColorMap();
		instance = this;
	}
	
	private void fillColorMap() {
		highlightId = (new Random()).nextInt() % 30;
	}
	
	@Override
	public void update(float time) {
		if (isFadingIn) {
			alpha = Math.min(1.0f, alpha + time);
			if (alpha == 1.0f) {
				isFadingIn = false;
				
			}
		} else {
			alpha = Math.max(0.0f, alpha - time);
			if (alpha == 0.0f) {
				isFadingIn = true;
				fillColorMap();
				(new Thread(new Runnable() {
					@Override
					public void run() {
						map.resetImageLayer(layerId);
					}
				})).start();
			}
		}
	}
	
	@Override
	public void drawToCache(Fragment fragment) {
		int[] dataCache = Fragment.getIntArray();
		for (int i = 0; i < size*size; i++) {
			if (fragment.biomeData[i] == highlightId)
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
