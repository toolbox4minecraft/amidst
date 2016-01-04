package amidst.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JToggleButton.ToggleButtonModel;

import amidst.documentation.ThreadSafe;

@ThreadSafe
public class MultipleStringsSetting extends SettingBase<String> {
	@SuppressWarnings("serial")
	public class SelectButtonModel extends ToggleButtonModel {
		private final String name;

		public SelectButtonModel(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		@Override
		public boolean isSelected() {
			return name.equals(get());
		}

		@Override
		public void setSelected(boolean isSelected) {
			if (isSelected) {
				set(name);
			}
		}

		private void update() {
			super.setSelected(isSelected());
		}
	}

	private final Iterable<SelectButtonModel> buttonModels;

	public MultipleStringsSetting(Preferences preferences, String key,
			String defaultValue, String[] values) {
		super(preferences, key);
		this.buttonModels = createButtonModels(values);
		restore(defaultValue);
	}

	private Iterable<SelectButtonModel> createButtonModels(String[] values) {
		List<SelectButtonModel> result = new ArrayList<SelectButtonModel>(
				values.length);
		for (String value : values) {
			result.add(new SelectButtonModel(value));
		}
		return Collections.unmodifiableList(result);
	}

	@Override
	protected String getInitialValue(String defaultValue) {
		return preferences.get(key, defaultValue);
	}

	@Override
	protected void update(String value) {
		this.preferences.put(key, value);
		updateButtonModels();
	}

	private void updateButtonModels() {
		for (SelectButtonModel buttonModel : buttonModels) {
			buttonModel.update();
		}
	}

	public Iterable<SelectButtonModel> getButtonModels() {
		return buttonModels;
	}
}
