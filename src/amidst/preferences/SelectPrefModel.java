package amidst.preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JToggleButton.ToggleButtonModel;

import amidst.documentation.ThreadSafe;

@ThreadSafe
public class SelectPrefModel implements PrefModel<String> {
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
			return name.equals(value);
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

	private final Preferences preferences;
	private final String key;
	private volatile String value;

	private final Iterable<SelectButtonModel> buttonModels;

	public SelectPrefModel(Preferences preferences, String key,
			String defaultValue, String[] availableOptions) {
		this.key = key;
		this.preferences = preferences;
		this.buttonModels = createButtonModels(availableOptions);
		restore(defaultValue);
	}

	private void restore(String defaultValue) {
		set(preferences.get(key, defaultValue));
	}

	private Iterable<SelectButtonModel> createButtonModels(
			String[] availableOptions) {
		List<SelectButtonModel> result = new ArrayList<SelectButtonModel>(
				availableOptions.length);
		for (String option : availableOptions) {
			result.add(new SelectButtonModel(option));
		}
		return Collections.unmodifiableList(result);
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public String get() {
		return value;
	}

	@Override
	public synchronized void set(String value) {
		this.value = value;
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
