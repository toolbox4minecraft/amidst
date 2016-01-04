package amidst;

import java.awt.image.BufferedImage;
import java.util.Properties;

import amidst.documentation.Immutable;

@Immutable
public class AmidstMetaData {
	public static AmidstMetaData from(Properties properties, BufferedImage icon) {
		return new AmidstMetaData(icon, AmidstVersion.from(properties),
				properties.getProperty("amidst.gui.mainWindow.title"));
	}

	private final BufferedImage icon;
	private final AmidstVersion version;
	private final String mainWindowTitle;

	private AmidstMetaData(BufferedImage icon, AmidstVersion version,
			String mainWindowTitle) {
		this.icon = icon;
		this.version = version;
		this.mainWindowTitle = mainWindowTitle;
	}

	public BufferedImage getIcon() {
		return icon;
	}

	public AmidstVersion getVersion() {
		return version;
	}

	public String getMainWindowTitle() {
		return mainWindowTitle;
	}
}
