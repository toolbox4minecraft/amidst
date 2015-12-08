package amidst.preferences;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import amidst.logging.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class BiomeColorProfileLoader {
	private static final Gson GSON = new Gson();

	public void visitProfiles(BiomeColorProfileVisitor visitor) {
		visitProfiles(BiomeColorProfile.PROFILE_DIRECTORY, visitor);
	}

	public void visitProfiles(File directory, BiomeColorProfileVisitor visitor) {
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
			} catch (JsonSyntaxException e) {
				Log.w("Unable to load file: " + file);
				e.printStackTrace();
			} catch (IOException e) {
				Log.i("Unable to load file: " + file);
			}
		}
		return profile;
	}

	private BiomeColorProfile readProfile(File file)
			throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		BiomeColorProfile result = GSON.fromJson(reader,
				BiomeColorProfile.class);
		reader.close();
		return result;
	}
}
