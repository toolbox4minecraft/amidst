package amidst;

import java.awt.image.BufferedImage;
import java.util.Properties;

import amidst.documentation.Immutable;

@Immutable
public class AmidstMetaData {
	public static AmidstMetaData from(Properties properties, BufferedImage icon) {
		return new AmidstMetaData(icon, AmidstVersion.from(properties));
	}

	private final BufferedImage icon;
	private final AmidstVersion version;

	private AmidstMetaData(BufferedImage icon, AmidstVersion version) {
		this.icon = icon;
		this.version = version;
	}

	public BufferedImage getIcon() {
		return icon;
	}

	public AmidstVersion getVersion() {
		return version;
	}
}
