package amidst.devtools;

import amidst.AmidstVersion;
import amidst.ResourceLoader;
import amidst.gui.main.UpdateInformationJson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GenerateUpdateInformationJson {
	private static final String DOWNLOAD_URL = "https://github.com/toolbox4minecraft/amidst/releases";
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting()
			.create();

	public static void main(String[] args) {
		AmidstVersion version = AmidstVersion.from(ResourceLoader
				.getProperties("/amidst/metadata.properties"));
		if (version.isPreRelease()) {
			throw new RuntimeException(
					"Update information documents can only be created for stable releases (not a pre-release).");
		}
		UpdateInformationJson json = new UpdateInformationJson(
				version.getMajor(), version.getMinor(), "", DOWNLOAD_URL);
		System.out.println(GSON.toJson(json));
	}
}
