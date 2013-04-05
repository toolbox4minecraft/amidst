package amidst;

import javax.swing.JToggleButton.ToggleButtonModel;

/** Currently selected options that change AMIDST’s behavior
 * TODO: save and load
 */
public enum Options {
	instance;

	public ToggleButtonModel showSlimeChunks;
	public ToggleButtonModel showGrid;
	public ToggleButtonModel showNetherFortresses;
	public ToggleButtonModel showIcons;
	public boolean saveEnabled;
	
	private Options() {
		showSlimeChunks      = new ToggleButtonModel();
		showGrid             = new ToggleButtonModel();
		showNetherFortresses = new ToggleButtonModel();
		showIcons            = new ToggleButtonModel();
		showIcons.setSelected(true);
		saveEnabled = true;
	}
}
