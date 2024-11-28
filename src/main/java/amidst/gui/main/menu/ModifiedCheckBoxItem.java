package amidst.gui.main.menu;

import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;

public class ModifiedCheckBoxItem extends JCheckBoxMenuItem {
	private static final long serialVersionUID = -3691709624298884014L;

	public ModifiedCheckBoxItem() {
    }

	public ModifiedCheckBoxItem(Icon icon) {
        super(icon);
    }

	public ModifiedCheckBoxItem(String text) {
        super(text);
    }

	public ModifiedCheckBoxItem(Action a) {
        super(a);
    }

	public ModifiedCheckBoxItem(String text, Icon icon) {
        super(text, icon);
    }

	public ModifiedCheckBoxItem(String text, boolean b) {
        super(text, b);
    }
	
	public ModifiedCheckBoxItem(String text, Icon icon, boolean b) {
        super(text, icon, b);
    }
	
	@Override
	protected void processMouseEvent(MouseEvent evt) {
		if (evt.getID() == MouseEvent.MOUSE_RELEASED) {
			if (contains(evt.getPoint())) {
				doClick();
				setArmed(true);
			}
		} else {
			super.processMouseEvent(evt);
		}
	}
}
