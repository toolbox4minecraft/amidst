package amidst.map.layer;

import amidst.map.Fragment;
import amidst.minecraft.Biome;
import amidst.minecraft.world.Resolution;
import amidst.utilities.ColorUtils;

public class BiomeLayer extends ImageLayer {
	// TODO: remove me!
	private static BiomeLayer instance;

	@Deprecated
	public static BiomeLayer getInstance() {
		return instance;
	}

	private boolean[] selectedBiomes = new boolean[Biome.getBiomesLength()];
	private boolean isHighlightMode = false;

	public BiomeLayer(int layerId) {
		super(layerId, Resolution.QUARTER);
		instance = this;
	}

	@Override
	protected void drawToCache(Fragment fragment, int[] cache, long cornerX,
			long cornerY, int size) {
		short[][] biomeData = getWorld().getBiomeDataAt(fragment.getCorner());
		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {
				int index = getCacheIndex(x, y, size);
				cache[index] = getColor(biomeData[x][y]);
			}
		}
	}

	protected int getColor(int biome) {
		if (isDeselected(biome)) {
			return ColorUtils.deselectColor(doGetColor(biome));
		} else {
			return doGetColor(biome);
		}
	}

	@Deprecated
	@Override
	protected int getColorAt(Fragment fragment, long xAsResolution,
			long yAsResolution) {
		// TODO Auto-generated method stub
		return 0;
	}

	private boolean isDeselected(int biome) {
		return isHighlightMode && !selectedBiomes[biome];
	}

	private int doGetColor(int biome) {
		return Biome.getByIndex(biome).getColor();
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
