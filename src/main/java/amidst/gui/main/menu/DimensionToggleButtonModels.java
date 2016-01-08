package amidst.gui.main.menu;

import javax.swing.JToggleButton.ToggleButtonModel;

import amidst.documentation.ThreadSafe;
import amidst.gui.main.Actions;
import amidst.mojangapi.world.Dimension;

@ThreadSafe
public class DimensionToggleButtonModels {
	@SuppressWarnings("serial")
	public class DimensionButtonModel extends ToggleButtonModel {
		private final Dimension dimension;

		public DimensionButtonModel(Dimension dimension) {
			this.dimension = dimension;
		}

		@Override
		public boolean isSelected() {
			return dimension == get();
		}

		@Override
		public void setSelected(boolean isSelected) {
			if (isSelected) {
				set(dimension);
			}
		}

		private void update() {
			super.setSelected(isSelected());
		}
	}

	private final Actions actions;
	private final DimensionButtonModel overworld;
	private final DimensionButtonModel theEnd;
	private volatile Dimension selection;

	public DimensionToggleButtonModels(Actions actions) {
		this.actions = actions;
		this.overworld = new DimensionButtonModel(Dimension.OVERWORLD);
		this.theEnd = new DimensionButtonModel(Dimension.END);
		this.set(Dimension.OVERWORLD);
	}

	public ToggleButtonModel getOverworld() {
		return overworld;
	}

	public ToggleButtonModel getTheEnd() {
		return theEnd;
	}

	public Dimension get() {
		return selection;
	}

	public synchronized void set(Dimension value) {
		this.selection = value;
		this.overworld.update();
		this.theEnd.update();
		this.actions.selectDimension(value);
	}
}
