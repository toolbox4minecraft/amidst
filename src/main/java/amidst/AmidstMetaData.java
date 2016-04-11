package amidst;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import amidst.documentation.Immutable;

@Immutable
public class AmidstMetaData {
	public static AmidstMetaData from(Properties properties, BufferedImage... icons) {
		return new AmidstMetaData(Arrays.asList(icons), AmidstVersion.from(properties));
	}

	private final List<BufferedImage> icons;
	private final AmidstVersion version;

	private AmidstMetaData(List<BufferedImage> icons, AmidstVersion version) {
		this.icons = icons;
		this.version = version;
	}

	public List<BufferedImage> getIcons() {
		return icons;
	}

	public AmidstVersion getVersion() {
		return version;
	}
}
