package amidst;

import java.io.File;
import java.util.prefs.Preferences;

import amidst.minecraft.LocalMinecraftInstallation;
import amidst.preferences.BiomeColorProfile;
import amidst.preferences.BooleanPrefModel;
import amidst.preferences.FilePrefModel;
import amidst.preferences.SelectPrefModel;
import amidst.preferences.StringPreference;

/**
 * Currently selected options that change AMIDST’s behavior
 */
public enum Options {
	instance;

	// permanent preferences
	public final FilePrefModel jar;
	public final BooleanPrefModel showSlimeChunks;
	public final BooleanPrefModel showGrid;
	public final BooleanPrefModel showNetherFortresses;
	public final BooleanPrefModel showTemples;
	public final BooleanPrefModel showPlayers;
	public final BooleanPrefModel showStrongholds;
	public final BooleanPrefModel showVillages;
	public final BooleanPrefModel showOceanMonuments;
	public final BooleanPrefModel showSpawn;
	public final BooleanPrefModel mapFlicking;
	public final BooleanPrefModel mapFading;
	public final BooleanPrefModel showFPS;
	public final BooleanPrefModel showScale;
	public final BooleanPrefModel showDebug;
	public final BooleanPrefModel updateToUnstable;
	public final BooleanPrefModel maxZoom;

	public final StringPreference lastProfile;

	public final SelectPrefModel worldType;
	public BiomeColorProfile biomeColorProfile;

	private Options() {
		// @formatter:off
		Preferences pref = Preferences.userNodeForPackage(Amidst.class);
		jar				     = new FilePrefModel(   pref, "jar", new File(LocalMinecraftInstallation.getMinecraftDirectory(), "bin/minecraft.jar"));
		showSlimeChunks	     = new BooleanPrefModel(pref, "slimeChunks",	 	 false);
		showGrid			 = new BooleanPrefModel(pref, "grid",			 	 false);
		showNetherFortresses = new BooleanPrefModel(pref, "netherFortressIcons", false);
		mapFlicking		     = new BooleanPrefModel(pref, "mapFlicking",		 true);
		mapFading		  	 = new BooleanPrefModel(pref, "mapFading",		     true);
		maxZoom			     = new BooleanPrefModel(pref, "maxZoom",			 true);
		showStrongholds	     = new BooleanPrefModel(pref, "strongholdIcons",	 true);
		showPlayers		     = new BooleanPrefModel(pref, "playerIcons",		 true);
		showTemples		     = new BooleanPrefModel(pref, "templeIcons",		 true);
		showVillages		 = new BooleanPrefModel(pref, "villageIcons",		 true);
		showOceanMonuments	 = new BooleanPrefModel(pref, "oceanMonumentIcons",	 true);
		showSpawn			 = new BooleanPrefModel(pref, "spawnIcon",		     true);
		showFPS			     = new BooleanPrefModel(pref, "showFPS",			 true);
		showScale		     = new BooleanPrefModel(pref, "showScale",			 true);
		showDebug			 = new BooleanPrefModel(pref, "showDebug",		     false);
		updateToUnstable     = new BooleanPrefModel(pref, "updateToUnstable",    false);
		lastProfile          = new StringPreference(pref, "profile",             null);
		biomeColorProfile	 = new BiomeColorProfile();
		worldType			 = new SelectPrefModel( pref, "worldType",  "Prompt each time", new String[] { "Prompt each time", "Default", "Flat", "Large Biomes", "Amplified" });
		// @formatter:on
		biomeColorProfile.validate();
	}
}
