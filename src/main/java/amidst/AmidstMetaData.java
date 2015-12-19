package amidst;

import java.awt.Image;

import amidst.documentation.Immutable;

@Immutable
public enum AmidstMetaData {
	;

	public static final int MAJOR_VERSION = 3;
	public static final int MINOR_VERSION = 7;
	public static final String VERSION_OFFSET = "";

	public static final Image ICON = ResourceLoader
			.getImage("/amidst/icon.png");

	public static String getFullVersionString() {
		return MAJOR_VERSION + "." + MINOR_VERSION + VERSION_OFFSET;
	}
}
