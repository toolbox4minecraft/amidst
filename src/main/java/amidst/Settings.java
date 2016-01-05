package amidst;

import java.util.prefs.Preferences;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.WorldType;
import amidst.settings.BooleanSetting;
import amidst.settings.MultipleStringsSetting;
import amidst.settings.Setting;
import amidst.settings.StringSetting;
import amidst.settings.biomecolorprofile.BiomeColorProfile;
import amidst.settings.biomecolorprofile.BiomeColorProfileSelection;

@ThreadSafe
public class Settings {
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
	public Settings(Preferences preferences) {
		// @formatter:off
		showSlimeChunks            = new BooleanSetting(        preferences, "slimeChunks",         false);
		showGrid                   = new BooleanSetting(        preferences, "grid",                false);
		showNetherFortresses       = new BooleanSetting(        preferences, "netherFortressIcons", false);
		smoothScrolling            = new BooleanSetting(        preferences, "mapFlicking",         true);
		fragmentFading             = new BooleanSetting(        preferences, "mapFading",           true);
		maxZoom                    = new BooleanSetting(        preferences, "maxZoom",             true);
		showStrongholds            = new BooleanSetting(        preferences, "strongholdIcons",     true);
		showPlayers                = new BooleanSetting(        preferences, "playerIcons",         true);
		showTemples                = new BooleanSetting(        preferences, "templeIcons",         true);
		showVillages               = new BooleanSetting(        preferences, "villageIcons",        true);
		showOceanMonuments         = new BooleanSetting(        preferences, "oceanMonumentIcons",  true);
		showSpawn                  = new BooleanSetting(        preferences, "spawnIcon",           true);
		showFPS                    = new BooleanSetting(        preferences, "showFPS",             true);
		showScale                  = new BooleanSetting(        preferences, "showScale",           true);
		showDebug                  = new BooleanSetting(        preferences, "showDebug",           false);
		updateToUnstable           = new BooleanSetting(        preferences, "updateToUnstable",    false);
		lastProfile                = new StringSetting(         preferences, "profile",             "");
		worldType                  = new MultipleStringsSetting(preferences, "worldType",           WorldType.PROMPT_EACH_TIME, WorldType.getWorldTypeSettingAvailableValues());
		biomeColorProfileSelection = new BiomeColorProfileSelection(BiomeColorProfile.getDefaultProfile());
		// @formatter:on
	}
}
