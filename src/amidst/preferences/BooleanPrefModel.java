package amidst.preferences;

import javax.swing.JToggleButton.ToggleButtonModel;
import java.util.prefs.Preferences;

public class BooleanPrefModel extends ToggleButtonModel implements PrefModel<Boolean> {
	private static final long serialVersionUID = -2291122955784916836L;
	
	private final String key;
	private final Preferences pref;
	
	public BooleanPrefModel(Preferences pref, String key, boolean selected) {
		super();
		this.pref = pref;
		this.key = key;
		set(pref.getBoolean(key, selected));
	}
	
	@Override
	public String getKey() {
		return key;
	}
	
	@Override
	public Boolean get() {
		assert pref.get(key, null) != null && pref.getBoolean(key, false) == super.isSelected();
		return super.isSelected();
	}
	
	@Override
	public boolean isSelected() {
		return get();
	}
	
	@Override
	public void set(Boolean value) {
		super.setSelected(value);
		pref.putBoolean(key, value);
	}
	
	@Override
	public void setSelected(boolean value) {
		set(value);
	}
}
