package amidst.minetest.file;

import java.io.IOException;

import amidst.documentation.Immutable;
import amidst.gameengineabstraction.file.IUnresolvedLauncherProfile;
import amidst.minetest.MinetestLauncherProfile;
import amidst.minetest.file.directory.MinetestDirectory;
import amidst.mojangapi.file.LauncherProfile;
import amidst.mojangapi.file.VersionList;
import amidst.parsing.FormatException;

@Immutable
public class UnresolvedLauncherProfile implements IUnresolvedLauncherProfile {
	private final MinetestDirectory minetestDirectory;
	private final String name;

	public UnresolvedLauncherProfile(
			MinetestDirectory minetestDirectory,
			String name) {
		this.minetestDirectory = minetestDirectory;
		this.name = name;
	}

	public UnresolvedLauncherProfile() {
		this.minetestDirectory = null;
		this.name = MinetestLauncherProfile.InternalDefault.getProfileName();		
	}
	
	public String getName() {
		return name;
	}

	@Override
	public LauncherProfile resolve(VersionList versionList) throws FormatException, IOException {
		return new MinetestLauncherProfile(minetestDirectory, name);
	}

	@Override
	public LauncherProfile resolveToVanilla(VersionList versionList) throws FormatException, IOException {
		// Vanilla is a concept for modded Minecraft, Minetest doesn't need it.
		return resolve(versionList);
	}

}
