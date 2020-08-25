package amidst.gui.main;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import amidst.logging.AmidstLogger;
import amidst.util.OperatingSystemDetector;

public enum AmidstLookAndFeel {
	DEFAULT("Default Look & Feel", UIManager.getCrossPlatformLookAndFeelClassName()),
	SYSTEM("System Look & Feel", UIManager.getSystemLookAndFeelClassName());

	private final String displayName;
	private final String lookAndFeelClassName;

	private AmidstLookAndFeel(String displayName, String lookAndFeelClassName) {
		this.displayName = displayName;
		this.lookAndFeelClassName = lookAndFeelClassName;
	}

	public boolean isCurrent() {
		return lookAndFeelClassName.equals(getCurrent());
	}

	private static String getCurrent() {
		LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
		if (lookAndFeel == null) {
			return null;
		}
		return lookAndFeel.getClass().getName();
	}

	public String getDisplayName() {
		return displayName;
	}

	public boolean tryApply() {
		String currentLookAndFeel = getCurrent();
		boolean success = true;
		try {
			UIManager.setLookAndFeel(lookAndFeelClassName);
			AmidstLogger.info("Using look & feel: " + lookAndFeelClassName);
		} catch(ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			AmidstLogger.error(e, "Couldn't apply look & feel: " + lookAndFeelClassName +
					"; falling back to: " + currentLookAndFeel);
			success = false;
		}

		if (OperatingSystemDetector.isMac()) {
			OsXWorkarounds.applyWorkarounds();
		}

		return success;
	}

	@Override
	public String toString() {
		return getDisplayName();
	}
}
