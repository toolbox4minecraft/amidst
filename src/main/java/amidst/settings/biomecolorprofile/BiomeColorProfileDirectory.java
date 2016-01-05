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
public class BiomeColorProfileDirectory {
	public static BiomeColorProfileDirectory create(String root) {
		BiomeColorProfileDirectory result = new BiomeColorProfileDirectory(
				getRoot(root));
		Log.i("using biome color profiles at: '" + result.getRoot() + "'");
		return result;
	}

	private static File getRoot(String root) {
		if (root != null) {
			return new File(root);
		} else {
			return DEFAULT_ROOT_DIRECTORY;
		}
	}

	private static final File DEFAULT_ROOT_DIRECTORY = new File(
			"biome-color-profiles");
	private static final Gson GSON = new Gson();

	private final File root;
	private final File defaultProfile;

	public BiomeColorProfileDirectory(File root) {
		this.root = root;
		this.defaultProfile = new File(root, "default.json");
	}

	public File getRoot() {
		return root;
	}

	public File getDefaultProfile() {
		return defaultProfile;
	}

	public boolean isValid() {
		return root.isDirectory();
	}

	public void saveDefaultProfileIfNecessary() {
		if (!isValid()) {
			Log.i("Unable to find biome color profile directory.");
		} else {
			Log.i("Found biome color profile directory.");
			if (defaultProfile.isFile()) {
				Log.i("Found default biome color profile.");
			} else if (BiomeColorProfile.getDefaultProfile().save(
					defaultProfile)) {
				Log.i("Saved default biome color profile.");
			} else {
				Log.i("Attempted to save default biome color profile, but encountered an error.");
			}
		}
	}

	public void visitProfiles(BiomeColorProfileVisitor visitor) {
		visitProfiles(root, visitor);
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
				if (profile == null) {
					throw new NullPointerException();
				}
				profile.validate();
			} catch (JsonSyntaxException | JsonIOException | IOException
					| NullPointerException e) {
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
