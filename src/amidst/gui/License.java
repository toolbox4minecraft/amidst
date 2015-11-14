package amidst.gui;

import java.io.IOException;
import java.net.URISyntaxException;

import amidst.logging.Log;
import amidst.resources.ResourceLoader;

public class License {
	private String name;
	private String path;
	private String licenseText;

	public License(String name, String path) {
		this.name = name;
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public String getLicenseText() {
		if (licenseText == null) {
			loadLicenseText();
		}
		if (licenseText == null) {
			return "cannot read license text";
		} else {
			return licenseText;
		}
	}

	private void loadLicenseText() {
		try {
			licenseText = ResourceLoader.getResourceAsString(path);
		} catch (IOException e) {
			Log.w("Unable to read file: " + name + ".");
			e.printStackTrace();
		} catch (URISyntaxException e) {
			Log.w("Unable to read file: " + name + ".");
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return name;
	}
}
