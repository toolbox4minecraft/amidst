package amidst.mojangapi.world.icon.producer;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.world.icon.WorldIcon;

@NotThreadSafe
public class WorldIconCollector implements Consumer<WorldIcon> {
	private List<WorldIcon> worldIcons;

	@Override
	public void accept(WorldIcon worldIcon) {
		initListIfNecessary();
		worldIcons.add(worldIcon);
	}

	private void initListIfNecessary() {
		if (worldIcons == null) {
			worldIcons = new LinkedList<>();
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
