package amidst.devtools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import amidst.AmidstVersion;
import amidst.gui.main.UpdateInformationJson;

public class GenerateUpdateInformationJson {
	private static final String DOWNLOAD_URL = "https://github.com/toolbox4minecraft/amidst/releases";
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private final AmidstVersion version;

	public GenerateUpdateInformationJson(AmidstVersion version) {
		this.version = version;
	}

	public void run() {
		if (version.isPreRelease()) {
			throw new RuntimeException(
					"Update information documents can only be created for stable releases (not a pre-release).");
		}
		UpdateInformationJson json = new UpdateInformationJson(
				version.getMajor(),
				version.getMinor(),
				version.getPatch(),
				"",
				DOWNLOAD_URL);
		System.out.println(GSON.toJson(json));
	}
}
