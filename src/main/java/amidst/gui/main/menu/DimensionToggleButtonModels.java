package amidst.gui.main.menu;

import javax.swing.JToggleButton.ToggleButtonModel;

import amidst.documentation.ThreadSafe;
import amidst.fragment.dimension.DimensionIds;
import amidst.gui.main.Actions;

@ThreadSafe
public class DimensionToggleButtonModels {
	@SuppressWarnings("serial")
	public class DimensionIdButtonModel extends ToggleButtonModel {
		private final int dimensionId;

		public DimensionIdButtonModel(int dimensionId) {
			this.dimensionId = dimensionId;
		}

		public int getDimensionId() {
			return dimensionId;
		}

		@Override
		public boolean isSelected() {
			return dimensionId == get();
		}

		@Override
		public void setSelected(boolean isSelected) {
			if (isSelected) {
				set(dimensionId);
			}
		}

		private void update() {
			super.setSelected(isSelected());
		}
	}

	private final Actions actions;
	private final DimensionIdButtonModel overworld;
	private final DimensionIdButtonModel theEnd;
	private volatile int selection;

	public DimensionToggleButtonModels(Actions actions) {
		this.actions = actions;
		this.overworld = new DimensionIdButtonModel(DimensionIds.OVERWORLD);
		this.theEnd = new DimensionIdButtonModel(DimensionIds.THE_END);
		this.set(DimensionIds.OVERWORLD);
	}

	public ToggleButtonModel getOverworld() {
		return overworld;
	}

	public ToggleButtonModel getTheEnd() {
		return theEnd;
	}

	public int get() {
		return selection;
	}

	public synchronized void set(int dimensionId) {
		this.selection = dimensionId;
		this.overworld.update();
		this.theEnd.update();
		this.actions.selectDimension(dimensionId);
	}
}
