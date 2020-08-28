package amidst.gui.main;

import amidst.AmidstVersion;
import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;

@Immutable
public class UpdateInformationJson {
	private volatile int major;
	private volatile int minor;
	private volatile int patch;
	private volatile String message;
	private volatile String downloadUrl;

	@GsonConstructor
	public UpdateInformationJson() {
	}

	public UpdateInformationJson(int major, int minor, int patch, String message, String downloadUrl) {
		this.major = major;
		this.minor = minor;
		this.patch = patch;
		this.downloadUrl = downloadUrl;
		this.message = message;
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

	/**
	 * The message can be used to display additional information about the
	 * update.
	 */
	public String getMessage() {
		return message;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public AmidstVersion createAmidstVersion() {
		return new AmidstVersion(major, minor, patch);
	}
}
