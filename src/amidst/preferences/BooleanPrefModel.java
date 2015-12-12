package amidst.preferences;

import java.util.prefs.Preferences;

import javax.swing.JToggleButton.ToggleButtonModel;

import amidst.documentation.ThreadSafe;

@ThreadSafe
public class BooleanPrefModel implements PrefModel<Boolean> {
	@SuppressWarnings("serial")
	private class BooleanButtonModel extends ToggleButtonModel {
		@Override
		public boolean isSelected() {
			return value;
		}

		@Override
		public void setSelected(boolean isSelected) {
			set(isSelected);
		}

		private void update() {
			super.setSelected(value);
		}
	}

	private final Preferences preferences;
	private final String key;
	private volatile boolean value;

	private final BooleanButtonModel buttonModel;

	public BooleanPrefModel(Preferences preferences, String key,
			boolean defaultValue) {
		this.preferences = preferences;
		this.key = key;
		this.buttonModel = new BooleanButtonModel();
		restore(defaultValue);
	}

	private void restore(boolean defaultValue) {
		set(preferences.getBoolean(key, defaultValue));
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public Boolean get() {
		return value;
	}

	@Override
	public synchronized void set(Boolean value) {
		this.value = value;
		this.preferences.putBoolean(key, value);
		updateButtonModel();
	}

	private void updateButtonModel() {
		buttonModel.update();
	}

	public BooleanButtonModel getButtonModel() {
		return buttonModel;
	}
}
