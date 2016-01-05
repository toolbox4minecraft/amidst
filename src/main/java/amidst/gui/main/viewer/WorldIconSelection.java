package amidst.gui.main.viewer;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.icon.WorldIcon;

@ThreadSafe
public class WorldIconSelection {
	private volatile WorldIcon selection;

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
