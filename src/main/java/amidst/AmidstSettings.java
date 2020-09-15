package amidst;

import java.util.prefs.Preferences;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.ThreadSafe;
import amidst.gui.main.AmidstLookAndFeel;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.WorldType;
import amidst.settings.Setting;
import amidst.settings.biomeprofile.BiomeProfile;
import amidst.settings.biomeprofile.BiomeProfileSelection;

@ThreadSafe
public class AmidstSettings {
	public final Setting<Dimension> dimension;
	public final Setting<Boolean> showGrid;
	public final Setting<Boolean> showSlimeChunks;
	public final Setting<Boolean> showSpawn;
	public final Setting<Boolean> showStrongholds;
	public final Setting<Boolean> showPlayers;
	public final Setting<Boolean> showVillages;
	public final Setting<Boolean> showTemples;
	public final Setting<Boolean> showMineshafts;
	public final Setting<Boolean> showOceanMonuments;
	public final Setting<Boolean> showWoodlandMansions;
	public final Setting<Boolean> showOceanFeatures;
	public final Setting<Boolean> showNetherFortresses;
	public final Setting<Boolean> showEndCities;

	public final Setting<Boolean> smoothScrolling;
	public final Setting<Boolean> fragmentFading;
	public final Setting<Boolean> maxZoom;
	public final Setting<Boolean> showFPS;
	public final Setting<Boolean> showScale;
	public final Setting<Boolean> showDebug;
	public final Setting<Boolean> useHybridScaling;
	public final Setting<AmidstLookAndFeel> lookAndFeel;

	public final Setting<String> lastProfile;
	public final Setting<String> worldType;
	
	public final Setting<String> lastBiomeExportPath;
	public final Setting<String> lastScreenshotPath;

	/**
	 * This is not persisted.
	 */
	public final BiomeProfileSelection biomeProfileSelection;

	@CalledOnlyBy(AmidstThread.EDT)
	public AmidstSettings(Preferences preferences) {
		// @formatter:off
		dimension                  = Setting.createDimension(preferences, "dimension",            Dimension.OVERWORLD);
		showGrid                   = Setting.createBoolean(  preferences, "grid",                 false);
		showSlimeChunks            = Setting.createBoolean(  preferences, "slimeChunks",          false);
		showSpawn                  = Setting.createBoolean(  preferences, "spawnIcon",            true);
		showStrongholds            = Setting.createBoolean(  preferences, "strongholdIcons",      true);
		showPlayers                = Setting.createBoolean(  preferences, "playerIcons",          true);
		showVillages               = Setting.createBoolean(  preferences, "villageIcons",         true);
		showTemples                = Setting.createBoolean(  preferences, "templeIcons",          true);
		showMineshafts             = Setting.createBoolean(  preferences, "mineshaftIcons",       false);
		showOceanMonuments         = Setting.createBoolean(  preferences, "oceanMonumentIcons",   true);
		showWoodlandMansions       = Setting.createBoolean(  preferences, "woodlandMansionIcons", true);
		showOceanFeatures          = Setting.createBoolean(  preferences, "oceanFeaturesIcons",   true);
		showNetherFortresses       = Setting.createBoolean(  preferences, "netherFortressIcons",  false);
		showEndCities              = Setting.createBoolean(  preferences, "endCityIcons",         false);

		smoothScrolling            = Setting.createBoolean(  preferences, "mapFlicking",          true);
		fragmentFading             = Setting.createBoolean(  preferences, "mapFading",            true);
		maxZoom                    = Setting.createBoolean(  preferences, "maxZoom",              true);
		showFPS                    = Setting.createBoolean(  preferences, "showFPS",              true);
		showScale                  = Setting.createBoolean(  preferences, "showScale",            true);
		showDebug                  = Setting.createBoolean(  preferences, "showDebug",            false);
		useHybridScaling           = Setting.createBoolean(  preferences, "useHybridScaling",     true);
		lookAndFeel                = Setting.createEnum(     preferences, "lookAndFeel",          AmidstLookAndFeel.DEFAULT);

		lastProfile                = Setting.createString(   preferences, "profile",              "");
		worldType                  = Setting.createString(   preferences, "worldType",            WorldType.PROMPT_EACH_TIME);
		
		lastBiomeExportPath        = Setting.createString(   preferences, "lastBiomeExportPath",  "");
		lastScreenshotPath         = Setting.createString(   preferences, "lastScreenshotPath",   "");
		
		biomeProfileSelection = new BiomeProfileSelection(BiomeProfile.getDefaultProfile());
		// @formatter:on
	}
}
