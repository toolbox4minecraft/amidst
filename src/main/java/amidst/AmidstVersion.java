package amidst;

import java.util.Properties;

import amidst.documentation.Immutable;

@Immutable
public class AmidstVersion {
	public static AmidstVersion from(Properties properties) {
		return new AmidstVersion(
				Integer.parseInt(properties.getProperty("amidst.version.major")),
				Integer.parseInt(properties.getProperty("amidst.version.minor")),
				Integer.parseInt(properties.getProperty("amidst.version.patch")),
				properties.getProperty("amidst.version.preReleaseSuffix"));
	}

	private final int major;
	private final int minor;
	private final int patch;
	private final String preReleaseSuffix;

	public AmidstVersion(int major, int minor, int patch) {
		this(major, minor, patch, null);
	}

	public AmidstVersion(int major, int minor, int patch, String preReleaseSuffix) {
		this.major = major;
		this.minor = minor;
		this.patch = patch;
		this.preReleaseSuffix = preReleaseSuffix;
	}

	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
	}

	public int getPatch() {
		return patch;
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

	public boolean isNewerPatchVersionThan(AmidstVersion old) {
		return major == old.major && minor == old.minor && patch > old.patch;
	}

	public boolean isSameVersionButOldPreReleaseAndNewStable(AmidstVersion old) {
		return isSameVersion(old) && old.isPreRelease() && !isPreRelease();
	}

	public boolean isSameVersion(AmidstVersion old) {
		return major == old.major && minor == old.minor && patch == old.patch;
	}

	public boolean isPreRelease() {
		return preReleaseSuffix != null && !preReleaseSuffix.isEmpty();
	}

	public String createLongVersionString() {
		return "Amidst " + createVersionString();
	}

	public String createVersionString() {
		String version = "v" + major + "." + minor;
		if (patch != 0) {
			version += "." + patch;
		}
		if (isPreRelease()) {
			version += "-" + preReleaseSuffix;
		}
		return version;
	}
}
