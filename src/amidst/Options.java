package amidst;

import java.io.File;
import java.util.prefs.Preferences;

import amidst.map.BiomeColorProfileSelection;
import amidst.preferences.BiomeColorProfile;
import amidst.preferences.BooleanPrefModel;
import amidst.preferences.FilePrefModel;
import amidst.preferences.SelectPrefModel;
import amidst.preferences.StringPreference;

public class Options {
	private static final String[] WORLD_TYPE_OPTIONS = new String[] {
			"Prompt each time", "Default", "Flat", "Large Biomes", "Amplified" };

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
	public final BiomeColorProfileSelection biomeColorProfileSelection;

	public Options(Preferences pref, File defaultJarFile) {
		// @formatter:off
		jar				     = new FilePrefModel(   pref, "jar",                 defaultJarFile);
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
		worldType			 = new SelectPrefModel( pref, "worldType",           "Prompt each time", WORLD_TYPE_OPTIONS);
		biomeColorProfileSelection = new BiomeColorProfileSelection(BiomeColorProfile.getDefaultProfile());
		// @formatter:on
	}
}
