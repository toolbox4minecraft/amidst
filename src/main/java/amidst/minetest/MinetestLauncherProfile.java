package amidst.minetest;

import java.io.File;

import amidst.minetest.file.directory.MinetestDirectory;
import amidst.minetest.world.mapgen.MapgenParams;
import amidst.minetest.world.mapgen.MapgenV7Params;
import amidst.mojangapi.file.LauncherProfile;
import amidst.mojangapi.file.json.version.VersionJson;

public class MinetestLauncherProfile extends LauncherProfile {

	// Can be null! (minetest might not be installed, and we might be using
	// defaults)
	private final MinetestDirectory minetestDirectory;

	public static MinetestLauncherProfile InternalDefault = new MinetestLauncherProfile(
			null, "Minetest defaults");

	public MinetestLauncherProfile(MinetestDirectory minetestDirectory,
			String profileName) {

		super(null, // dotMinecraftDirectory,
				null, // profileDirectory,
				null, // versionDirectory,
				new VersionJson("0.5", null), // versionJson,
				true, // isVersionListedInProfile,
				profileName);

		this.minetestDirectory = minetestDirectory;

		// TODO Auto-generated constructor stub
	}

	public MapgenParams getMapGenParams() {

		// TODO: fetch the MapgenV7Params from MinetestDirectory
		return new MapgenV7Params();
	}

	@Override
	public File getSaves() {
		// Amidst currently only supports loading Minecraft games
		// and from here we dunno where the minecraft saves are kept.
		return null;
	}
	

}
