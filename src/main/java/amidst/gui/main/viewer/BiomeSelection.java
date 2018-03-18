package amidst.gui.main.viewer;

import java.security.InvalidParameterException;
import java.util.concurrent.atomic.AtomicBoolean;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.biome.Biome;

@ThreadSafe
public class BiomeSelection {
	private final AtomicBoolean[] selectedBiomes;
	private volatile AtomicBoolean isHighlightMode = new AtomicBoolean(false);

	public BiomeSelection() {
		this.selectedBiomes = createSelectedBiomes();
	}

	private AtomicBoolean[] createSelectedBiomes() {
		AtomicBoolean[] result = new AtomicBoolean[Biome.getBiomesLength()];
		for (int i = 0; i < result.length; i++) {
			result[i] = new AtomicBoolean(false);
		}
		return result;
	}

	public boolean isSelected(int id) {
		if (id >= selectedBiomes.length) {
			throw new InvalidParameterException("BiomeSelection requires refactoring - biome id greater than " + (selectedBiomes.length - 1) + " was requested!");
		} else if (id < 0) {
			// Less then zero indicates Unknown/NONE, which biome oracles are allowed to return.
			return false;
		}
		return !isHighlightMode.get() || selectedBiomes[id].get();
	}

	public void selectAll() {
		setAll(true);
	}

	public void deselectAll() {
		setAll(false);
	}

	public void toggle(int id) {
		if (id >= selectedBiomes.length) {
			throw new InvalidParameterException("BiomeSelection requires refactoring - biome id greater than " + (selectedBiomes.length - 1) + " was requested!");
		} else if (id >= 0) {
			// Biome isn't Invalid/NONE 
			// (less then zero indicates Unknown/NONE, which biome oracles are allowed to return))
			toggle(selectedBiomes[id]);
		}
	}

	private void setAll(boolean value) {
		for (AtomicBoolean selectedBiome : selectedBiomes) {
			selectedBiome.set(value);
		}
	}

	public void selectOnlySpecial() {
		for (int i = 0; i < selectedBiomes.length; i++) {
			selectedBiomes[i].set(Biome.isSpecialBiomeIndex(i));
		}
	}

	public void toggleHighlightMode() {
		toggle(isHighlightMode);
	}

	public boolean isHighlightMode() {
		return isHighlightMode.get();
	}

	private void toggle(AtomicBoolean atomicBoolean) {
		boolean value;
		do {
			value = atomicBoolean.get();
		} while (!atomicBoolean.compareAndSet(value, !value));
	}
}
