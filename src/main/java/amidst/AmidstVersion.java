package amidst;

import java.util.Properties;

import amidst.documentation.Immutable;

@Immutable
public class AmidstVersion {
	public static AmidstVersion from(Properties properties) {
		return new AmidstVersion(
				Integer.parseInt(properties.getProperty("amidst.version.major")),
				Integer.parseInt(properties.getProperty("amidst.version.minor")),
				properties.getProperty("amidst.version.preReleaseSuffix"));
	}

	private final int major;
	private final int minor;
	private final String preReleaseSuffix;

	public AmidstVersion(int major, int minor) {
		this(major, minor, null);
	}

	public AmidstVersion(int major, int minor, String preReleaseSuffix) {
		this.major = major;
		this.minor = minor;
		this.preReleaseSuffix = preReleaseSuffix;
	}

	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
	}

	public String getPreReleaseSuffix() {
		return preReleaseSuffix;
	}

	public boolean isNewerMajorVersionThan(AmidstVersion old) {
		return major > old.major;
	}

	public boolean isNewerMinorVersionThan(AmidstVersion old) {
		return major == old.major && minor > old.minor;
	}

	public boolean isSameVersionButOldPreReleaseAndNewStable(AmidstVersion old) {
		return isSameVersion(old) && old.isPreRelease() && !isPreRelease();
	}

	public boolean isSameVersion(AmidstVersion old) {
		return major == old.major && minor == old.minor;
	}

	public boolean isPreRelease() {
		return preReleaseSuffix != null && !preReleaseSuffix.isEmpty();
	}

	public String createLongVersionString() {
		return "Amidst " + createVersionString();
	}

	public String createVersionString() {
		if (isPreRelease()) {
			return "v" + major + "." + minor + "-" + preReleaseSuffix;
		} else {
			return "v" + major + "." + minor;
		}
	}
}
