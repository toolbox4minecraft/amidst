package amidst.settings.biomeprofile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import amidst.documentation.Immutable;
import amidst.logging.Log;

@Immutable
public class BiomeProfileDirectory {
	public static BiomeProfileDirectory create(String root) {
		BiomeProfileDirectory result = new BiomeProfileDirectory(getRoot(root));
		Log.i("using biome profiles at: '" + result.getRoot() + "'");
		return result;
	}

	private static File getRoot(String root) {
		if (root != null) {
			return new File(root);
		} else {
			return DEFAULT_ROOT_DIRECTORY;
		}
	}

	private static final File DEFAULT_ROOT_DIRECTORY = new File("biome");
	private static final Gson GSON = new Gson();

	private final File root;
	private final File defaultProfile;

	public BiomeProfileDirectory(File root) {
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
			Log.i("Unable to find biome profile directory.");
		} else {
			Log.i("Found biome profile directory.");
			if (defaultProfile.isFile()) {
				Log.i("Found default biome profile.");
			} else if (BiomeProfile.getDefaultProfile().save(defaultProfile)) {
				Log.i("Saved default biome profile.");
			} else {
				Log.i("Attempted to save default biome profile, but encountered an error.");
			}
		}
	}

	public void visitProfiles(BiomeProfileVisitor visitor) {
		visitProfiles(root, visitor);
	}

	private void visitProfiles(File directory, BiomeProfileVisitor visitor) {
		boolean entered = false;
		for (File file : directory.listFiles()) {
			if (file.isFile()) {
				BiomeProfile profile = createFromFile(file);
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

	private BiomeProfile createFromFile(File file) {
		BiomeProfile profile = null;
		if (file.exists() && file.isFile()) {
			try {
				profile = readProfile(file);
				if (profile == null) {
					throw new NullPointerException();
				}
				profile.validate();
			} catch (JsonSyntaxException | JsonIOException | IOException | NullPointerException e) {
				Log.w("Unable to load file: " + file);
				e.printStackTrace();
			}
		}
		return profile;
	}

	private BiomeProfile readProfile(File file) throws IOException, JsonSyntaxException, JsonIOException {
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			return GSON.fromJson(reader, BiomeProfile.class);
		}
	}
}
