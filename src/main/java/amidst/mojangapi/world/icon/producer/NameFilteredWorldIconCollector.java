package amidst.mojangapi.world.icon.producer;

import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.world.icon.WorldIcon;

@NotThreadSafe
public class NameFilteredWorldIconCollector extends WorldIconCollector {
	private final String name;

	public NameFilteredWorldIconCollector(String name) {
		this.name = name;
	}

	@Override
	public void accept(WorldIcon worldIcon) {
		if (worldIcon.getName().equals(name)) {
			super.accept(worldIcon);
		}
	}
}
