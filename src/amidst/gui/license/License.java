package amidst.gui.license;

import amidst.documentation.Immutable;
import amidst.logging.Log;
import amidst.resources.ResourceLoader;

@Immutable
public class License {
	private final String name;
	private final String licenseText;

	public License(String name, String path) {
		this.name = name;
		this.licenseText = readLicenseText(path);
	}

	public String readLicenseText(String path) {
		try {
			return ResourceLoader.getResourceAsString(path);
		} catch (Exception e) {
			Log.w("Unable to read license file: " + name + ".");
			e.printStackTrace();
			return "License text is missing.";
		}
	}

	public String getName() {
		return name;
	}

	public String getLicenseText() {
		return licenseText;
	}

	@Override
	public String toString() {
		return name;
	}
}
