package amidst.settings.biomeprofile;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

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

	@SuppressWarnings("unchecked")
	private Collection<BiomeProfile> createDefaultProfiles() {		
		try {
			Method staticMethod = biomeProfileImpl.getMethod("getDefaultProfiles");
			Object defaultProfile = staticMethod.invoke(null);
			return (Collection<BiomeProfile>)defaultProfile;
		} catch (Exception e) {
			throw new RuntimeException("BiomeProfile implementations must implement getDefaultProfiles(): " + e);
		}
	}
	
	/**
	 * Returns a profile suitable for the current game engine
	 * @param optional_suggestion - can be null, if not null then the profile with this name
	 * will be returned if the directory has it.
	 */
	public BiomeProfile getProfile(String optional_suggestion) {
		BiomeProfile result = null;
		if (optional_suggestion != null && optional_suggestion.length() > 0) {
			result = createFromName(optional_suggestion);
		}
		if (result == null) {		
			// Return the first profile that biomeProfileImpl provides 
			result = createDefaultProfiles().iterator().next();
		}
		return result;
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
	}
	
	public void saveDefaultProfilesIfNecessary() {
		if (!isValid()) {
			AmidstLogger.info("Unable to find biome profile directory.");
		} else {
			AmidstLogger.info("Found biome profile directory.");
			for(BiomeProfile profile : createDefaultProfiles()) {						
				File profileFile = new File(root, profile.getName() + "." + profileFileExtension);
				
				if (profileFile.isFile()) {
					AmidstLogger.info("Found default biome profile \"" + profile.getName() + "\"");
				} else if (profile.save(profileFile)) {
					AmidstLogger.info("Saved default biome profile \"" + profile.getName() + "\"");
				} else {
					AmidstLogger.info("Attempted to save default biome profile \"" + profile.getName() + "\", but encountered an error.");
				}				
			}
		}
	}

	public void visitProfiles(BiomeProfileVisitor visitor) {
		visitProfiles(root, visitor);
	}

	private void visitProfiles(File directory, BiomeProfileVisitor visitor) {
		boolean entered = false;
		
		for (File file : getBiomeProfileFileList(directory)) {
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
	
	private File[] getBiomeProfileFileList(File directory) {
		FilenameFilter filter = new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		        return name.toLowerCase().endsWith("." + profileFileExtension);
		    }
		};		
		return directory.listFiles(filter);		
	}

	private BiomeProfile createFromName(String name) {
		// Unfortunately we can't assume the name matches the file name,
		// so load every biome file until we find the right one.
		
		for (File file : getBiomeProfileFileList(root)) {
			if (file.isFile()) {
				BiomeProfile profile = createFromFile(file);
				if (profile != null && profile.getName().equals(name)) {
					return profile;				
				}
			}
		}
		return null;		
	}
	
	
	private BiomeProfile createFromFile(File file) {
		BiomeProfile profile = null;
		if (file.exists() && file.isFile()) {
			try {
				profile = JsonReader.readLocation(file, biomeProfileImpl);
				if (!profile.validate()) throw new FormatException("validate() failed");
			} catch (IOException | FormatException e) {
				AmidstLogger.warn(e, "Unable to load file: " + file);
			}
		}
		return profile;
	}
}
