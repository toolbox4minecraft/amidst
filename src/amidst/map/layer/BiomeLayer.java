package amidst.map.layer;

import amidst.map.Fragment;
import amidst.minecraft.Biome;
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
		super(Fragment.SIZE >> 2, layerId);
		instance = this;
	}

	@Override
	public void drawToCache(Fragment fragment, int[] cache) {
		for (int blockY = 0; blockY < getSize(); blockY++) {
			for (int blockX = 0; blockX < getSize(); blockX++) {
				int i = blockY * getSize() + blockX;
				cache[i] = getColorAt(fragment, blockX, blockY);
			}
		}
	}

	private int getColorAt(Fragment fragment, int blockX, int blockY) {
		if (isHighlightMode) {
			return drawAtInHighlightMode(fragment, blockX, blockY);
		} else {
			return drawAtInNormalMode(fragment, blockX, blockY);
		}
	}

	private int drawAtInHighlightMode(Fragment fragment, int blockX, int blockY) {
		if (selectedBiomes[fragment.getBiomeAtUsingBlockCoordinates(blockX,
				blockY)]) {
			return drawAtInNormalMode(fragment, blockX, blockY);
		} else {
			return ColorUtils.deselectColor(getColor(fragment, blockX, blockY));
		}
	}

	private int drawAtInNormalMode(Fragment fragment, int blockX, int blockY) {
		return getColor(fragment, blockX, blockY);
	}

	private int getColor(Fragment fragment, int blockX, int blockY) {
		return Biome.getByIndex(
				fragment.getBiomeAtUsingBlockCoordinates(blockX, blockY))
				.getColor();
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
