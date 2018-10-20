package amidst.settings.biomeprofile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Map.Entry;

import amidst.AmidstSettings;
import amidst.AmidstVersion;
import amidst.documentation.Immutable;
import amidst.logging.AmidstLogger;
import amidst.minetest.world.mapgen.DefaultBiomes;
import amidst.parsing.FormatException;
import amidst.parsing.json.JsonReader;
import amidst.util.OperatingSystemDetector;

@Immutable
public class BiomeProfileDirectory {
	
	public static BiomeProfileDirectory create(String root) {
		BiomeProfileDirectory result = new BiomeProfileDirectory(getRoot(root));
		AmidstLogger.info("using biome profiles at: '" + result.getRoot() + "'");
		return result;
	}

	private static File getRoot(String root) {
		File result = null;		
		
		if (root != null) {
			result = new File(root);
			tryCreateDirectory(result);
			if (!(result.isDirectory() && result.canWrite())) result = null;
		}
		if (result == null) result = getWriteableRootDirectory();
		if (result == null) {
			// We can't find a writable directory
			result = (root != null) ? new File(root) : DEFAULT_ROOT_DIRECTORY;
		}
		return result;
	}

	/**
	 * Returns null, or a writeable biome directory.
	 */
	private static File getWriteableRootDirectory() {
		
		File result = DEFAULT_ROOT_DIRECTORY;
		tryCreateDirectory(result);
		
		if (!(result.isDirectory() && result.canWrite() && result.canRead())) {
			// biome menu won't work without a directory with write access
			// Try using the location the OS has designated for app data.
			
			File home = new File(System.getProperty("user.home", "."));
			if (OperatingSystemDetector.isWindows()) {
				File appData = new File(System.getenv("APPDATA"));
				if (appData.isDirectory()) {
					result = new File(appData, AmidstVersion.getDataName());
				}
			} else if (OperatingSystemDetector.isMac()) {
				result = new File(home, "Library/Application Support/" + AmidstVersion.getDataName());
			} else {
				result = new File(home, "." + AmidstVersion.getDataName().toLowerCase());
			}
		}
		tryCreateDirectory(result);
		
		return (result.isDirectory() && result.canWrite()) ? result : null;
	}
	
	private static void tryCreateDirectory(File directory) {
		if (!directory.exists()) {
			try {
				directory.mkdirs();
			} catch (Exception ex) {
				AmidstLogger.error("Could not create directory \"" + directory.toString() + "\": " + ex.getMessage());
			}
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
	
	public void saveDefaultProfilesIfNecessary(AmidstSettings settings) {
		if (!isValid()) {
			AmidstLogger.info("Unable to find biome profile directory.");
		} else {
			AmidstLogger.info("Found biome profile directory.");
			
			removeObsoleteBiomeProfileFiles(settings);
			
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

	/**
	 * Delete any old biome profile files that are out of date, or have had their name changed.
	 * This only deletes known files, to prevent removal of anything user-created or user-edited. 
	 */
	private void removeObsoleteBiomeProfileFiles(AmidstSettings settings) {
		
		long obsoleteLastRemoved_epochDay = settings.lastBiomeProfilePurge.get();
		
		if ((obsoleteLastRemoved_epochDay == 0) || (obsoleteLastRemoved_epochDay < DefaultBiomes.getObsoleteBiomeProfilesDate().toEpochDay())) {
			// The data we have about obsolete biome profiles is more recent than the 
			// last time we removed obsolete biome profiles, so do so again.
			try {
				MessageDigest shaDigest = MessageDigest.getInstance("SHA-1");
				
				// Realistically it's only the Minetest biome profiles that will need older files removed 
				for(Entry<String, String> entry : DefaultBiomes.getObsoleteBiomeProfiles()) {
					
					File obsoleteFile = new File(root, entry.getKey() + "." + profileFileExtension);
					if (obsoleteFile.isFile()) {
						// check the file content matches the obsolete content, i.e. hasn't been edited by the user
						if (entry.getValue().equals(getFileChecksum(shaDigest, obsoleteFile))) {
							AmidstLogger.info("Removing obsolete biome profile \"" + obsoleteFile.getName() + "\"");
							obsoleteFile.delete();
						}
					}
				}
				// Save the date the cleanup was performed
				settings.lastBiomeProfilePurge.set(LocalDate.now().toEpochDay());
			} catch(Exception ex) {
				// cleaning up obsolete files is not critical
				AmidstLogger.error("Exception removing old biome profiles: " + ex.getMessage());
			}
		}
	}
	
	private static String getFileChecksum(MessageDigest digest, File file) throws IOException
	{
	    //Get file input stream for reading the file content
	    FileInputStream fis = new FileInputStream(file);
	     
	    //Create byte array to read data in chunks
	    byte[] byteArray = new byte[1024];
	    int bytesCount = 0;
	      
	    //Read file data and update in message digest
	    while ((bytesCount = fis.read(byteArray)) != -1) {
	        digest.update(byteArray, 0, bytesCount);
	    };
	     
	    //close the stream; We don't need it now.
	    fis.close();
	     
	    //Get the hash's bytes
	    byte[] bytes = digest.digest();
	     
	    //This bytes[] has bytes in decimal format;
	    //Convert it to hexadecimal format
	    StringBuilder sb = new StringBuilder();
	    for(int i=0; i< bytes.length ;i++)
	    {
	        sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
	    }
	     
	    //return complete hash
	    return sb.toString();
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
		if (isValid()) {
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
		} else {
			// We don't have a biome directory, see if the defaults contains this biome 
			for (BiomeProfile profile : createDefaultProfiles()) {
				if (profile != null && profile.getName().equals(name)) return profile;				
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
