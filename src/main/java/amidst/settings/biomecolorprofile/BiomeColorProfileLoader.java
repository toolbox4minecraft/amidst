package amidst.settings.biomecolorprofile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import amidst.documentation.Immutable;
import amidst.logging.Log;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

@Immutable
public class BiomeColorProfileLoader {
	private static final Gson GSON = new Gson();

	public void visitProfiles(BiomeColorProfileVisitor visitor) {
		visitProfiles(BiomeColorProfile.PROFILE_DIRECTORY, visitor);
	}

	private void visitProfiles(File directory, BiomeColorProfileVisitor visitor) {
		boolean entered = false;
		for (File file : directory.listFiles()) {
			if (file.isFile()) {
				BiomeColorProfile profile = createFromFile(file);
				if (profile != null) {
					if (!entered) {
						entered = true;
						visitor.enterDirectory(directory.getName());
					}
					visitor.visitProfile(profile);
				}
			} else {
				visitProfiles(file, visitor);
			}
		}
		if (entered) {
			visitor.leaveDirectory();
		}
	}

	private BiomeColorProfile createFromFile(File file) {
		BiomeColorProfile profile = null;
		if (file.exists() && file.isFile()) {
			try {
				profile = readProfile(file);
				profile.validate();
			} catch (JsonSyntaxException | JsonIOException | IOException e) {
				Log.w("Unable to load file: " + file);
				e.printStackTrace();
			}
		}
		return profile;
	}

	private BiomeColorProfile readProfile(File file) throws IOException,
			JsonSyntaxException, JsonIOException {
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			return GSON.fromJson(reader, BiomeColorProfile.class);
		}
	}
}
