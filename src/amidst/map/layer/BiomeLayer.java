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
	protected int getColorAt(Fragment fragment, int blockX, int blockY) {
		int biome = fragment.getBiomeAtUsingQuarterResolution(blockX, blockY);
		if (isDeselected(biome)) {
			return ColorUtils.deselectColor(getColor(biome));
		} else {
			return getColor(biome);
		}
	}

	private boolean isDeselected(int biome) {
		return isHighlightMode && !selectedBiomes[biome];
	}

	private int getColor(int biome) {
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
