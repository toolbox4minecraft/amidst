package amidst.map.layer;

import amidst.Util;
import amidst.map.Fragment;
import amidst.minecraft.Biome;

public class BiomeLayer extends ImageLayer {
	// TODO: remove me!
	private static BiomeLayer instance;

	@Deprecated
	public static BiomeLayer getInstance() {
		return instance;
	}

	private boolean[] selectedBiomes = new boolean[Biome.biomes.length];
	private boolean isHighlightMode = false;

	public BiomeLayer() {
		super(Fragment.SIZE >> 2);
		instance = this;
	}

	@Override
	public void drawToCache(Fragment fragment) {
		int[] dataCache = Fragment.getImageRGBDataCache();
		if (isHighlightMode) {
			drawHighlightMode(fragment, dataCache);
		} else {
			drawNormalMode(fragment, dataCache);
		}
		fragment.setImageRGB(getLayerId(), dataCache);
	}

	private void drawHighlightMode(Fragment fragment, int[] dataCache) {
		for (int i = 0; i < getSquaredSize(); i++) {
			if (!selectedBiomes[fragment.getBiomeData()[i]]) {
				dataCache[i] = Util.deselectColor(getColor(fragment, i));
			} else {
				dataCache[i] = getColor(fragment, i);
			}
		}
	}

	private void drawNormalMode(Fragment fragment, int[] dataCache) {
		for (int i = 0; i < getSquaredSize(); i++) {
			dataCache[i] = getColor(fragment, i);
		}
	}

	private int getColor(Fragment fragment, int index) {
		return Biome.biomes[fragment.getBiomeData()[index]].color;
	}

	public boolean isBiomeSelected(int id) {
		return selectedBiomes[id];
	}

	public void selectAllBiomes() {
		setSelectedAllBiomes(true);
	}

	public void deselectAllBiomes() {
		setSelectedAllBiomes(false);
	}

	public void selectBiome(int id) {
		setSelected(id, true);
	}

	public void deselectBiome(int id) {
		setSelected(id, false);
	}

	public void toggleBiomeSelect(int id) {
		setSelected(id, !selectedBiomes[id]);
	}

	private void setSelected(int id, boolean value) {
		selectedBiomes[id] = value;
	}

	private void setSelectedAllBiomes(boolean value) {
		for (int i = 0; i < selectedBiomes.length; i++) {
			selectedBiomes[i] = value;
		}
	}

	public void selectOnlySpecialBiomes() {
		for (int i = 0; i < selectedBiomes.length; i++) {
			selectedBiomes[i] = i >= 128;
		}
	}

	public void setHighlightMode(boolean enabled) {
		this.isHighlightMode = enabled;
	}
}
