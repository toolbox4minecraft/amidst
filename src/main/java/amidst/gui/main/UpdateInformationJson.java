package amidst.gui.main;

import amidst.AmidstVersion;
import amidst.documentation.GsonObject;
import amidst.documentation.Immutable;

@Immutable
@GsonObject
public class UpdateInformationJson {
	private volatile int major;
	private volatile int minor;
	private volatile String message;
	private volatile String downloadUrl;
	
	public UpdateInformationJson() {
	}

	public UpdateInformationJson(int major, int minor, String message, String downloadUrl) {
		this.major = major;
		this.minor = minor;
		this.downloadUrl = downloadUrl;
		this.message = message;
	}

	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
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
		return new AmidstVersion(major, minor);
	}
}
