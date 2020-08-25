package amidst.settings.biomeprofile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import amidst.documentation.Immutable;
import amidst.logging.AmidstLogger;
import amidst.parsing.FormatException;
import amidst.parsing.json.JsonReader;

@Immutable
public class BiomeProfileDirectory {
	public static BiomeProfileDirectory create(Path biomeProfilesDirectory) {
	    if (biomeProfilesDirectory == null) {
	        biomeProfilesDirectory = DEFAULT_ROOT_DIRECTORY;
	    }
		BiomeProfileDirectory result = new BiomeProfileDirectory(biomeProfilesDirectory);
		AmidstLogger.info("using biome profiles at: '" + result.getRoot() + "'");
		return result;
	}

	private static final Path DEFAULT_ROOT_DIRECTORY = Paths.get("biome");

	private final Path root;

	public BiomeProfileDirectory(Path root) {
		this.root = root;
	}

	public Path getRoot() {
		return root;
	}

	public boolean isValid() {
		return Files.isDirectory(root);
	}

	public boolean visitProfiles(BiomeProfileVisitor visitor) {
		return visitProfiles(root, visitor);
	}

	private boolean visitProfiles(Path directory, BiomeProfileVisitor visitor) {
		boolean[] entered = new boolean[]{ false };

		try {
			Files.list(directory).forEachOrdered(file -> {
				if (Files.isRegularFile(file)) {
					BiomeProfile profile = createFromFile(file);
					if (profile != null) {
						if (!entered[0]) {
							entered[0] = true;
							visitor.enterDirectory(directory.getFileName().toString());
						}
						visitor.visitProfile(profile);
					}
				} else {
					visitProfiles(file, visitor);
				}
			});
		} catch (IOException e) {
			AmidstLogger.error(e, "Unexpected IO error while visiting biomes profiles.");
		}

		if (entered[0]) {
			visitor.leaveDirectory();
		}
		
		return entered[0];
	}

	private BiomeProfile createFromFile(Path file) {
		try {
			BiomeProfile profile = JsonReader.readLocation(file, BiomeProfile.class);
			if(profile.validate()) {
				return profile;
			}
			AmidstLogger.warn("Profile invalid, ignoring: {}", file);
		} catch (IOException | FormatException e) {
			try {
				BiomeProfile newProfile = JsonReader.readLocation(file, BiomeProfileOld.class).convertToNewFormat();
				if(newProfile.validate()) {
					newProfile.save(file);
					AmidstLogger.info("Profile converted to new format: {}", file);
					return newProfile;
				}
				AmidstLogger.warn("Profile invalid, ignoring: {}", file);
			} catch (Exception e1) {
				AmidstLogger.warn(e, "Unable to load file: {}", file);
			}
		}
		return null;
	}
}
