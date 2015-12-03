package amidst.map;

import amidst.minecraft.world.icon.WorldIcon;

public class WorldIconSelection {
	private WorldIcon selection;

	public WorldIcon getSelection() {
		return selection;
	}

	public void setSelection(WorldIcon selection) {
		this.selection = selection;
	}

	public boolean isSelected(WorldIcon worldIcon) {
		return selection == worldIcon;
	}

	public boolean hasSelection() {
		return selection != null;
	}
}
