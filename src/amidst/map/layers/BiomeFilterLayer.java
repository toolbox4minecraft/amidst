package amidst.map.layers;

public class BiomeFilterLayer extends BiomeLayer {

	public BiomeFilterLayer() {
		deselectAllBiomes();
		alpha = 0.0f;
	}
	
	
	
	@Override
	public void update(float time) {
		/*if (isFadingIn) {
			alpha = Math.min(1.0f, alpha + time);
			if (alpha == 1.0f) {
				isFadingIn = false;
				if (testId > 0)
					MapViewer.biomeLayer.selectBiome(testId - 1);
				MapViewer.biomeLayer.selectBiome(testId++);
				(new Thread(new Runnable() {
					@Override
					public void run() {
						map.resetImageLayer(MapViewer.biomeLayer.getLayerId());
					}
				})).start();
			}
		} else {
			alpha = Math.max(0.0f, alpha - time);
			if (alpha == 0.0f) {
				isFadingIn = true;
				if (testId > 0)
					selectBiome(testId - 1);
				selectBiome(testId++);
				(new Thread(new Runnable() {
					@Override
					public void run() {
						map.resetImageLayer(layerId);
					}
				})).start();
			}
		}*/
	}
	
	@Override
	public float getAlpha() {
		return alpha;
	}
	
	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}
	
	@Override
	public boolean isVisible() {
		return alpha != 0.0f;
	}
}
