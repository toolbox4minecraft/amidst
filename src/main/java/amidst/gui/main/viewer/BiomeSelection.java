package amidst.gui.main.viewer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.biome.BiomeList;

@ThreadSafe
public class BiomeSelection {
	private final BiomeList biomeList;
	private final ConcurrentHashMap<Integer, AtomicBoolean> selectedBiomes;
	private volatile AtomicBoolean isHighlightMode = new AtomicBoolean(false);

	public BiomeSelection(BiomeList biomeList) {
		this.biomeList = biomeList;
		this.selectedBiomes = createSelectedBiomes();
	}

	private ConcurrentHashMap<Integer, AtomicBoolean> createSelectedBiomes() {
		ConcurrentHashMap<Integer, AtomicBoolean> result = new ConcurrentHashMap<Integer, AtomicBoolean>(biomeList.size());
		for (Biome b : biomeList.iterable()) {
			result.put(b.getId(), new AtomicBoolean(false));
		}
		return result;
	}

	public boolean isSelected(int id) {
		return selectedBiomes.get(id).get();
	}

	public boolean isVisible(int id) {
		return !isHighlightMode.get() || isSelected(id);
	}

	public void selectAll() {
		setAll(true);
	}

	public void deselectAll() {
		setAll(false);
	}

	public void toggle(int id) {
		toggle(selectedBiomes.get(id));
	}

	private void setAll(boolean value) {
		for (AtomicBoolean selectedBiome : selectedBiomes.values()) {
			selectedBiome.set(value);
		}
	}

	public void selectOnlySpecial() {
		for (Biome b : biomeList.iterable()) {
			selectedBiomes.get(b.getId()).set(b.isSpecialBiome());
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
