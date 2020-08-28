package amidst.gui.main.viewer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import amidst.documentation.ThreadSafe;

@ThreadSafe
public class BiomeSelection {
	private final ConcurrentHashMap<Integer, AtomicBoolean> selectedBiomes = new ConcurrentHashMap<>();
	// Should newly encountered biomes be selected by default or not?
	private final AtomicBoolean unknownBiomesSelected = new AtomicBoolean(false);
	private final AtomicBoolean isHighlightMode = new AtomicBoolean(false);
	private final AtomicBoolean shouldWidgetBeVisible = new AtomicBoolean(false);

	public BiomeSelection() {
	}

	public boolean toggleWidgetVisibility() {
		return toggle(shouldWidgetBeVisible);
	}

	public boolean isWidgetVisible() {
		return shouldWidgetBeVisible.get();
	}

	public boolean isSelected(int id) {
		return getSelected(id).get();
	}

	private AtomicBoolean getSelected(int id) {
		return selectedBiomes.computeIfAbsent(id, i -> {
			return new AtomicBoolean(unknownBiomesSelected.get());
		});
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

	public boolean toggle(int id) {
		return toggle(getSelected(id));
	}

	private void setAll(boolean value) {
		selectedBiomes.clear();
		unknownBiomesSelected.set(value);
	}

	public boolean toggleHighlightMode() {
		return toggle(isHighlightMode);
	}

	public boolean isHighlightMode() {
		return isHighlightMode.get();
	}

	private boolean toggle(AtomicBoolean atomicBoolean) {
		boolean value;
		do {
			value = atomicBoolean.get();
		} while (!atomicBoolean.compareAndSet(value, !value));
		return !value;
	}
}
