package amidst.gameengineabstraction.file;

import java.io.IOException;

import amidst.mojangapi.file.LauncherProfile;
import amidst.mojangapi.file.VersionList;
import amidst.parsing.FormatException;

public interface IUnresolvedLauncherProfile {

	public String getName();

	public LauncherProfile resolve(VersionList versionList) throws FormatException, IOException;

	public LauncherProfile resolveToVanilla(VersionList versionList) throws FormatException, IOException;
	
}
