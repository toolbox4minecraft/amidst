package amidst;

import java.util.prefs.Preferences;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.WorldType;
import amidst.settings.Setting;
import amidst.settings.Settings;
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
	public final Setting<Boolean> showNetherFortresses;
	public final Setting<Boolean> showEndCities;
	public final Setting<Boolean> enableAllLayers;

	public final Setting<Boolean> smoothScrolling;
	public final Setting<Boolean> fragmentFading;
	public final Setting<Boolean> maxZoom;
	public final Setting<Boolean> showFPS;
	public final Setting<Boolean> showScale;
	public final Setting<Boolean> showDebug;

	public final Setting<String> lastProfile;
	public final Setting<String> worldType;

	/**
	 * This is not persisted.
	 */
	public final BiomeProfileSelection biomeProfileSelection;

	@CalledOnlyBy(AmidstThread.EDT)
	public AmidstSettings(Preferences preferences) {
		// @formatter:off
		dimension                  = Settings.createDimension(preferences, "dimension",            Dimension.OVERWORLD);
		showGrid                   = Settings.createBoolean(  preferences, "grid",                 false);
		showSlimeChunks            = Settings.createBoolean(  preferences, "slimeChunks",          false);
		showSpawn                  = Settings.createBoolean(  preferences, "spawnIcon",            true);
		showStrongholds            = Settings.createBoolean(  preferences, "strongholdIcons",      true);
		showPlayers                = Settings.createBoolean(  preferences, "playerIcons",          true);
		showVillages               = Settings.createBoolean(  preferences, "villageIcons",         true);
		showTemples                = Settings.createBoolean(  preferences, "templeIcons",          true);
		showMineshafts             = Settings.createBoolean(  preferences, "mineshaftIcons",       false);
		showOceanMonuments         = Settings.createBoolean(  preferences, "oceanMonumentIcons",   true);
		showWoodlandMansions       = Settings.createBoolean(  preferences, "woodlandMansionIcons", true);
		showNetherFortresses       = Settings.createBoolean(  preferences, "netherFortressIcons",  false);
		showEndCities              = Settings.createBoolean(  preferences, "endCityIcons",         false);
		enableAllLayers            = Settings.createBoolean(  preferences, "enableAllLayers",      false);
		
		smoothScrolling            = Settings.createBoolean(  preferences, "mapFlicking",          true);
		fragmentFading             = Settings.createBoolean(  preferences, "mapFading",            true);
		maxZoom                    = Settings.createBoolean(  preferences, "maxZoom",              true);
		showFPS                    = Settings.createBoolean(  preferences, "showFPS",              true);
		showScale                  = Settings.createBoolean(  preferences, "showScale",            true);
		showDebug                  = Settings.createBoolean(  preferences, "showDebug",            false);
	
		lastProfile                = Settings.createString(   preferences, "profile",              "");
		worldType                  = Settings.createString(   preferences, "worldType",            WorldType.PROMPT_EACH_TIME);
		biomeProfileSelection = new BiomeProfileSelection(BiomeProfile.getDefaultProfile());
		// @formatter:on
	}
}
