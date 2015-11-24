package amidst.map;

import amidst.minecraft.Biome;

public class BiomeSelection {
	private boolean[] selectedBiomes = new boolean[Biome.getBiomesLength()];
	private boolean isHighlightMode = false;

	public boolean isSelected(int id) {
		return !isHighlightMode || selectedBiomes[id];
	}

	public void selectAll() {
		setSelectedAll(true);
	}

	public void deselectAll() {
		setSelectedAll(false);
	}

	public void select(int id) {
		setSelected(id, true);
	}

	public void deselect(int id) {
		setSelected(id, false);
	}

	public void toggleSelect(int id) {
		setSelected(id, !selectedBiomes[id]);
	}

	private void setSelected(int id, boolean value) {
		selectedBiomes[id] = value;
	}

	private void setSelectedAll(boolean value) {
		for (int i = 0; i < selectedBiomes.length; i++) {
			selectedBiomes[i] = value;
		}
	}

	public void selectOnlySpecial() {
		for (int i = 0; i < selectedBiomes.length; i++) {
			selectedBiomes[i] = i >= 128;
		}
	}

	public void toggleHighlightMode() {
		isHighlightMode = !isHighlightMode;
	}

	public boolean isHighlightMode() {
		return isHighlightMode;
	}
}
