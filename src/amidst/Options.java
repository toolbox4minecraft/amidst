package amidst;

import javax.swing.JToggleButton.ToggleButtonModel;

/** Currently selected options that change AMIDST’s behavior
 * TODO: save and load
 */
public enum Options {
	instance;
	
	public ToggleButtonModel showIcons;
	public ToggleButtonModel showNetherFortresses;
	public boolean saveEnabled;
	
	private Options() {
		showIcons = new ToggleButtonModel();
		showNetherFortresses = new ToggleButtonModel();
		saveEnabled = true;
	}
}
