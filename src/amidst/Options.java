package amidst;

import java.util.prefs.Preferences;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.settings.BooleanSetting;
import amidst.settings.MultipleStringsSetting;
import amidst.settings.Setting;
import amidst.settings.StringSetting;
import amidst.settings.biomecolorprofile.BiomeColorProfile;
import amidst.settings.biomecolorprofile.BiomeColorProfileSelection;

public class Options {
	private static final String[] WORLD_TYPE_OPTIONS = new String[] {
			"Prompt each time", "Default", "Flat", "Large Biomes", "Amplified" };

	public final BooleanSetting showSlimeChunks;
	public final BooleanSetting showGrid;
	public final BooleanSetting showNetherFortresses;
	public final BooleanSetting showTemples;
	public final BooleanSetting showPlayers;
	public final BooleanSetting showStrongholds;
	public final BooleanSetting showVillages;
	public final BooleanSetting showOceanMonuments;
	public final BooleanSetting showSpawn;
	public final BooleanSetting smoothScrolling;
	public final BooleanSetting fragmentFading;
	public final BooleanSetting showFPS;
	public final BooleanSetting showScale;
	public final BooleanSetting showDebug;
	public final BooleanSetting updateToUnstable;
	public final BooleanSetting maxZoom;
	public final Setting<String> lastProfile;
	public final MultipleStringsSetting worldType;
	public final BiomeColorProfileSelection biomeColorProfileSelection;

	@CalledOnlyBy(AmidstThread.EDT)
	public Options(Preferences pref) {
		// @formatter:off
		showSlimeChunks            = new BooleanSetting(pref, "slimeChunks",         false);
		showGrid                   = new BooleanSetting(pref, "grid",                false);
		showNetherFortresses       = new BooleanSetting(pref, "netherFortressIcons", false);
		smoothScrolling            = new BooleanSetting(pref, "mapFlicking",         true);
		fragmentFading             = new BooleanSetting(pref, "mapFading",           true);
		maxZoom                    = new BooleanSetting(pref, "maxZoom",             true);
		showStrongholds            = new BooleanSetting(pref, "strongholdIcons",     true);
		showPlayers                = new BooleanSetting(pref, "playerIcons",         true);
		showTemples                = new BooleanSetting(pref, "templeIcons",         true);
		showVillages               = new BooleanSetting(pref, "villageIcons",        true);
		showOceanMonuments         = new BooleanSetting(pref, "oceanMonumentIcons",  true);
		showSpawn                  = new BooleanSetting(pref, "spawnIcon",           true);
		showFPS                    = new BooleanSetting(pref, "showFPS",             true);
		showScale                  = new BooleanSetting(pref, "showScale",           true);
		showDebug                  = new BooleanSetting(pref, "showDebug",           false);
		updateToUnstable           = new BooleanSetting(pref, "updateToUnstable",    false);
		lastProfile                = new StringSetting(pref, "profile",             null);
		worldType                  = new MultipleStringsSetting( pref, "worldType",           "Prompt each time", WORLD_TYPE_OPTIONS);
		biomeColorProfileSelection = new BiomeColorProfileSelection(BiomeColorProfile.getDefaultProfile());
		// @formatter:on
	}
}
