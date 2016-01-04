package amidst;

import java.awt.image.BufferedImage;
import java.util.Properties;

import amidst.documentation.Immutable;

@Immutable
public class AmidstMetaData {
	public static AmidstMetaData from(Properties properties, BufferedImage icon) {
		// @formatter:off
		return new AmidstMetaData(
				icon,
				Integer.parseInt(properties.getProperty("amidst.version.major")),
				Integer.parseInt(properties.getProperty("amidst.version.minor")),
				properties.getProperty("amidst.gui.mainWindow.title"));
		// @formatter:on
	}

	private final BufferedImage icon;
	private final int majorVersion;
	private final int minorVersion;
	private final String mainWindowTitle;

	private AmidstMetaData(BufferedImage icon, int majorVersion,
			int minorVersion, String mainWindowTitle) {
		this.icon = icon;
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
		this.mainWindowTitle = mainWindowTitle;
	}

	public BufferedImage getIcon() {
		return icon;
	}

	public int getMajorVersion() {
		return majorVersion;
	}

	public int getMinorVersion() {
		return minorVersion;
	}

	public String getMainWindowTitle() {
		return mainWindowTitle;
	}
}
