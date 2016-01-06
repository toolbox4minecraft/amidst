package amidst.mojangapi.world.icon.producer;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.world.icon.WorldIcon;

@NotThreadSafe
public class WorldIconCollector implements WorldIconConsumer {
	private List<WorldIcon> worldIcons;

	@Override
	public void consume(WorldIcon worldIcon) {
		initListIfNecessary();
		worldIcons.add(worldIcon);
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
