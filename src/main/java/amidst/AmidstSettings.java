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
public class AmidstSettings {
	public final BooleanSetting showSlimeChunks;
	public final BooleanSetting showGrid;
	public final BooleanSetting showSpawn;
	public final BooleanSetting showStrongholds;
	public final BooleanSetting showPlayers;
	public final BooleanSetting showVillages;
	public final BooleanSetting showTemples;
	public final BooleanSetting showMineshafts;
	public final BooleanSetting showNetherFortresses;
	public final BooleanSetting showOceanMonuments;
	public final BooleanSetting showEndCities;

	public final BooleanSetting smoothScrolling;
	public final BooleanSetting fragmentFading;
	public final BooleanSetting maxZoom;
	public final BooleanSetting showFPS;
	public final BooleanSetting showScale;
	public final BooleanSetting showDebug;

	public final Setting<String> lastProfile;
	public final MultipleStringsSetting worldType;
	public final BiomeColorProfileSelection biomeColorProfileSelection;

	@CalledOnlyBy(AmidstThread.EDT)
	public AmidstSettings(Preferences preferences) {
		// @formatter:off
		showSlimeChunks            = new BooleanSetting(        preferences, "slimeChunks",         false);
		showGrid                   = new BooleanSetting(        preferences, "grid",                false);
		showSpawn                  = new BooleanSetting(        preferences, "spawnIcon",           true);
		showStrongholds            = new BooleanSetting(        preferences, "strongholdIcons",     true);
		showPlayers                = new BooleanSetting(        preferences, "playerIcons",         true);
		showVillages               = new BooleanSetting(        preferences, "villageIcons",        true);
		showTemples                = new BooleanSetting(        preferences, "templeIcons",         true);
		showMineshafts             = new BooleanSetting(        preferences, "mineshaftIcons",      false);
		showNetherFortresses       = new BooleanSetting(        preferences, "netherFortressIcons", false);
		showOceanMonuments         = new BooleanSetting(        preferences, "oceanMonumentIcons",  true);
		showEndCities              = new BooleanSetting(        preferences, "endCityIcons",        false);
		
		smoothScrolling            = new BooleanSetting(        preferences, "mapFlicking",         true);
		fragmentFading             = new BooleanSetting(        preferences, "mapFading",           true);
		maxZoom                    = new BooleanSetting(        preferences, "maxZoom",             true);
		showFPS                    = new BooleanSetting(        preferences, "showFPS",             true);
		showScale                  = new BooleanSetting(        preferences, "showScale",           true);
		showDebug                  = new BooleanSetting(        preferences, "showDebug",           false);
	
		lastProfile                = new StringSetting(         preferences, "profile",             "");
		worldType                  = new MultipleStringsSetting(preferences, "worldType",           WorldType.PROMPT_EACH_TIME, WorldType.getWorldTypeSettingAvailableValues());
		biomeColorProfileSelection = new BiomeColorProfileSelection(BiomeColorProfile.getDefaultProfile());
		// @formatter:on
	}
}
