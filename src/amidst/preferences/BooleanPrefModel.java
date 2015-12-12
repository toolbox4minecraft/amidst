package amidst.preferences;

import java.util.prefs.Preferences;

import javax.swing.JToggleButton.ToggleButtonModel;

import amidst.documentation.ThreadSafe;

@ThreadSafe
public class BooleanPrefModel extends PrefModelBase<Boolean> {
	@SuppressWarnings("serial")
	private class BooleanButtonModel extends ToggleButtonModel {
		@Override
		public boolean isSelected() {
			return get();
		}

		@Override
		public void setSelected(boolean isSelected) {
			set(isSelected);
		}

		private void update() {
			super.setSelected(isSelected());
		}
	}

	private final BooleanButtonModel buttonModel;

	public BooleanPrefModel(Preferences preferences, String key,
			boolean defaultValue) {
		super(preferences, key, defaultValue);
		this.buttonModel = new BooleanButtonModel();
	}

	@Override
	protected Boolean getInitialValue(Boolean defaultValue) {
		return preferences.getBoolean(key, defaultValue);
	}

	@Override
	protected void update(Boolean value) {
		this.preferences.putBoolean(key, value);
		this.buttonModel.update();
	}

	public BooleanButtonModel getButtonModel() {
		return buttonModel;
	}
}
