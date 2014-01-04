package amidst.preferences;

import java.util.prefs.Preferences;

import javax.swing.JToggleButton.ToggleButtonModel;

public class SelectPrefModel implements PrefModel<String> {
	public class SelectButtonModel extends ToggleButtonModel {
		private SelectPrefModel model; 
		public String name;
		public SelectButtonModel(SelectPrefModel model, String name) {
			this.model = model;
			this.name = name;
			super.setSelected(false);
		}
		
		@Override
		public boolean isSelected() {
			return model.get().equals(name);
		}
		
		@Override
		public void setSelected(boolean value) {
			super.setSelected(value);
			if (value)
				model.set(name);
		}

		public String getName() {
			return name;
		}
	}
	private Preferences preferences;
	private String key;
	private String selected;
	private SelectButtonModel buttonModels[];
	public SelectPrefModel(Preferences pref, String key, String selected, String[] names) {
		this.key = key;
		this.preferences = pref;
		this.selected = selected;
		buttonModels = new SelectButtonModel[names.length];
		for (int i = 0; i < buttonModels.length; i++)
			buttonModels[i] = new SelectButtonModel(this, names[i]);
		set(pref.get(key, selected));
		
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public String get() { 
		return selected;
	}
	
	public SelectButtonModel[] getButtonModels() {
		return buttonModels;
	}

	@Override
	public void set(String value) {
		preferences.put(key, value);
		selected = value;
		for (int i = 0; i < buttonModels.length; i++)
			if (!value.equals(buttonModels[i].name))
				buttonModels[i].setSelected(false);
	}
}
