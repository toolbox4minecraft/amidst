package amidst.gameengineabstraction.file;

import java.io.File;
import java.io.IOException;
import java.util.List;

import amidst.mojangapi.file.SaveGame;
import amidst.parsing.FormatException;

/**
 * base interface for MinetestInstallation and MinecraftInstallation
 */
public interface IGameInstallation {

	public List<IUnresolvedLauncherProfile> readLauncherProfiles() throws FormatException, IOException;
	
	public SaveGame newSaveGame(File location) throws IOException, FormatException;
	
	
}
