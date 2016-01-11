package amidst.settings;

import java.util.prefs.Preferences;

import javax.swing.JToggleButton.ToggleButtonModel;

import amidst.documentation.ThreadSafe;

@ThreadSafe
public class BooleanSetting extends SettingBase<Boolean> {
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

	public BooleanSetting(Preferences preferences, String key,
			boolean defaultValue) {
		super(preferences, key);
		this.buttonModel = new BooleanButtonModel();
		restore(defaultValue);
	}

	@Override
	protected Boolean getInitialValue(Boolean defaultValue) {
		return preferences.getBoolean(key, defaultValue);
	}

	@Override
	protected void doSet(Boolean value) {
		this.preferences.putBoolean(key, value);
		this.buttonModel.update();
	}

	public BooleanButtonModel getButtonModel() {
		return buttonModel;
	}
}
