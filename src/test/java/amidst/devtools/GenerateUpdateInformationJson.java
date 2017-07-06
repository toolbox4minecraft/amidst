package amidst.devtools;

import com.google.gson.Gson;

import amidst.AmidstVersion;
import amidst.gui.main.UpdateInformationJson;
import amidst.parsing.json.GsonProvider;

public class GenerateUpdateInformationJson {
	private static final String DOWNLOAD_URL = "https://github.com/toolbox4minecraft/amidst/releases";
	private static final Gson GSON = GsonProvider.builder().setPrettyPrinting().create();

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
				"",
				DOWNLOAD_URL);
		System.out.println(GSON.toJson(json));
	}
}
