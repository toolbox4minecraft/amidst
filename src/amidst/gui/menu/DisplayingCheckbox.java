package amidst.gui.menu;

import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;

public class DisplayingCheckbox extends JCheckBoxMenuItem {
	public DisplayingCheckbox(String text, BufferedImage icon, int key,
			JToggleButton.ToggleButtonModel model) {
		super(text, (icon != null) ? new ImageIcon(icon) : null);
		if (key != -1)
			setAccelerator(KeyStroke.getKeyStroke(key,
					InputEvent.CTRL_DOWN_MASK));
		setModel(model);
	}
}
