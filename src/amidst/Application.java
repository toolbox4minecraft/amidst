package amidst;

import java.io.File;
import java.net.MalformedURLException;

import MoF.Project;
import amidst.gui.LicenseWindow;
import amidst.gui.MapWindow;
import amidst.gui.UpdatePrompt;
import amidst.gui.version.VersionSelectWindow;
import amidst.logging.Log;
import amidst.minecraft.IMinecraftInterface;
import amidst.minecraft.Minecraft;
import amidst.minecraft.MinecraftUtil;
import amidst.minecraft.remote.RemoteMinecraft;
import amidst.minecraft.world.FileWorld;
import amidst.minecraft.world.World;
import amidst.version.MinecraftProfile;

public class Application {
	private UpdatePrompt updateManager = new UpdatePrompt();
	private VersionSelectWindow versionSelectWindow;
	private MapWindow mapWindow;
	private Project project;
	private World world;

	public MapWindow getMapWindow() {
		return mapWindow;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
		mapWindow.setProject(project);
	}

	public void displayVersionSelectWindow() {
		setVersionSelectWindow(new VersionSelectWindow(this));
		setMapWindow(null);
	}

	public void displayMapWindow(RemoteMinecraft minecraftInterface) {
		displayMapWindow(minecraftInterface);
	}

	public void displayMapWindow(MinecraftProfile profile) {
		Util.setProfileDirectory(profile.getGameDir());
		displayMapWindow(createLocalMinecraftInterface(profile.getJarFile()));
	}

	public void displayMapWindow(String jarFile, String gameDirectory) {
		Util.setProfileDirectory(gameDirectory);
		displayMapWindow(createLocalMinecraftInterface(new File(jarFile)));
	}

	private void displayMapWindow(IMinecraftInterface minecraftInterface) {
		MinecraftUtil.setBiomeInterface(minecraftInterface);
		setMapWindow(new MapWindow(this));
		setVersionSelectWindow(null);
	}

	private IMinecraftInterface createLocalMinecraftInterface(File jarFile) {
		try {
			return new Minecraft(jarFile).createInterface();
		} catch (MalformedURLException e) {
			Log.crash(e, "MalformedURLException on Minecraft load.");
			return null;
		}
	}

	private void setVersionSelectWindow(VersionSelectWindow versionSelectWindow) {
		if (this.versionSelectWindow != null) {
			this.versionSelectWindow.dispose();
		}
		this.versionSelectWindow = versionSelectWindow;
	}

	private void setMapWindow(MapWindow mapWindow) {
		if (this.mapWindow != null) {
			this.mapWindow.dispose();
		}
		this.mapWindow = mapWindow;
	}

	// TODO: call me!
	public void dispose() {
		setVersionSelectWindow(null);
		setMapWindow(null);
		project.dispose();
	}

	public void displayLicenseWindow() {
		new LicenseWindow();
	}

	public void checkForUpdates() {
		updateManager.check(mapWindow);
	}

	public void checkForUpdatesSilently() {
		updateManager.checkSilently(mapWindow);
	}

	public void exitGracefully() {
		System.exit(0);
	}

	public World getWorld() {
		return world;
	}

	public boolean isFileWorld() {
		return world instanceof FileWorld;
	}

	public FileWorld getWorldAsFileWorld() {
		return (FileWorld) world;
	}

	public void setWorld(World world) {
		this.world = world;
		if (world != null) {
			setProject(new Project(this, world));
		}
	}
}
