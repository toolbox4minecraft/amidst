package amidst;

import java.util.Properties;

import amidst.documentation.Immutable;

@Immutable
public class AmidstVersion {
	public static AmidstVersion from(Properties properties) {
		return new AmidstVersion(
				Integer.parseInt(properties.getProperty("amidst.version.major")),
				Integer.parseInt(properties.getProperty("amidst.version.minor")));
	}

	private final int major;
	private final int minor;

	public AmidstVersion(int major, int minor) {
		this.major = major;
		this.minor = minor;
	}

	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
	}

	public boolean isNewerMajorVersionThan(AmidstVersion old) {
		return major > old.major;
	}

	public boolean isNewerMinorVersionThan(AmidstVersion old) {
		return major == old.major && minor > old.minor;
	}
}
