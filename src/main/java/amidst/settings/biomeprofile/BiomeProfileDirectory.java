package amidst.settings.biomeprofile;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import amidst.documentation.Immutable;
import amidst.logging.AmidstLogger;
import amidst.parsing.FormatException;
import amidst.parsing.json.JsonReader;

@Immutable
public class BiomeProfileDirectory {
	public static BiomeProfileDirectory create(String root) {
		BiomeProfileDirectory result = new BiomeProfileDirectory(getRoot(root));
		AmidstLogger.info("using biome profiles at: '" + result.getRoot() + "'");
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

	private final File root;
	private File defaultProfile;
	private String profileFileExtension;
	private Class<? extends BiomeProfile> biomeProfileImpl;

	public BiomeProfileDirectory(File root) {
		this.root = root;		
		// default to MineCraft BiomeProfiles
		selectGameEngine("mc", BiomeProfileImpl.class);
	}

	public File getRoot() {
		return root;
	}

	private BiomeProfile createDefaultProfile() {
		
		try {
			Method staticMethod = biomeProfileImpl.getMethod("getDefaultProfile");
			Object defaultProfile = staticMethod.invoke(null);
			return (BiomeProfile)defaultProfile;
		} catch (Exception e) {
			throw new RuntimeException("BiomeProfile implementations must implement getDefaultProfile(): " + e);
		}
	}
	
	public boolean isValid() {
		return root.isDirectory();
	}

	/**
	 * Minetest biomes are dynamic and specified by the BiomeProfile, whereas Minecraft biomes
	 * are hardcoded and only their color is specified by the BiomeProfile, so we keep profiles 
	 * for different engines separate, and invoke this method to switch between. 
	 * @param fileExtension
	 * @param biomeProfile
	 */
	public void selectGameEngine(String profile_file_extension, Class<? extends BiomeProfile> biome_profile_impl) {

		this.profileFileExtension = profile_file_extension;
		this.biomeProfileImpl = biome_profile_impl;
		this.defaultProfile = new File(getRoot(), "default." + profile_file_extension);
	}
	
	public void saveDefaultProfileIfNecessary() {
		if (!isValid()) {
			AmidstLogger.info("Unable to find biome profile directory.");
		} else {
			AmidstLogger.info("Found biome profile directory.");
			if (defaultProfile.isFile()) {
				AmidstLogger.info("Found default biome profile.");
			} else if (createDefaultProfile().save(defaultProfile)) {
				AmidstLogger.info("Saved default biome profile.");
			} else {
				AmidstLogger.info("Attempted to save default biome profile, but encountered an error.");
			}
		}
	}

	public void visitProfiles(BiomeProfileVisitor visitor) {
		visitProfiles(root, visitor);
	}

	private void visitProfiles(File directory, BiomeProfileVisitor visitor) {
		boolean entered = false;
		
		FilenameFilter filter = new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		        return name.toLowerCase().endsWith("." + profileFileExtension);
		    }
		};
		
		for (File file : directory.listFiles(filter)) {
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
				profile = JsonReader.readLocation(file, biomeProfileImpl);
				profile.validate();
			} catch (IOException | FormatException e) {
				AmidstLogger.warn(e, "Unable to load file: " + file);
			}
		}
		return profile;
	}
}
