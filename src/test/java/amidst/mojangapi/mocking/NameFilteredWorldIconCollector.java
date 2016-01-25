package amidst.mojangapi.mocking;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.world.icon.WorldIcon;

@NotThreadSafe
public class NameFilteredWorldIconCollector implements Consumer<WorldIcon> {
	private List<WorldIcon> worldIcons;
	private final String name;

	public NameFilteredWorldIconCollector(String name) {
		this.name = name;
	}

	@Override
	public void accept(WorldIcon worldIcon) {
		if (worldIcon.getName().equals(name)) {
			initListIfNecessary();
			worldIcons.add(worldIcon);
		}
	}

	private void initListIfNecessary() {
		if (worldIcons == null) {
			worldIcons = new LinkedList<WorldIcon>();
		}
	}

	public List<WorldIcon> get() {
		if (worldIcons == null) {
			return Collections.emptyList();
		} else {
			return worldIcons;
		}
	}
}
