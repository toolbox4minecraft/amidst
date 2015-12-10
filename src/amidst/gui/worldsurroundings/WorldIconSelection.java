package amidst.gui.worldsurroundings;

import amidst.mojangapi.world.icon.WorldIcon;

public class WorldIconSelection {
	private WorldIcon selection;

	public WorldIcon get() {
		return selection;
	}

	public void select(WorldIcon selection) {
		this.selection = selection;
	}

	public void clear() {
		this.selection = null;
	}

	public boolean isSelected(WorldIcon worldIcon) {
		return selection == worldIcon;
	}

	public boolean hasSelection() {
		return selection != null;
	}
}
