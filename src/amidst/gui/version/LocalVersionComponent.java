package amidst.gui.version;

import amidst.Application;
import amidst.logging.Log;
import amidst.mojangapi.dotminecraft.DotMinecraftDirectory;
import amidst.mojangapi.dotminecraft.VersionDirectory;
import amidst.mojangapi.launcherprofiles.LaucherProfileJson;
import amidst.mojangapi.versionlist.VersionListJson;

public class LocalVersionComponent extends VersionComponent {
	private final Application application;
	private final LaucherProfileJson profile;
	private final VersionDirectory version;

	public LocalVersionComponent(Application application,
			LaucherProfileJson profile,
			DotMinecraftDirectory dotMinecraftDirectory,
			VersionListJson versionList) {
		this.application = application;
		this.profile = profile;
		this.version = load(dotMinecraftDirectory, versionList);
		initComponent();
	}

	// TODO: move to worker
	private VersionDirectory load(DotMinecraftDirectory dotMinecraftDirectory,
			VersionListJson versionList) {
		VersionDirectory result = profile.createVersionDirectory(
				dotMinecraftDirectory, versionList);
		if (result != null) {
			return result;
		} else {
			Log.w("Unable to load version directory for profile: "
					+ profile.getName());
			return null;
		}
	}

	public String getProfileName() {
		return profile.getName();
	}

	@Override
	public boolean isReadyToLoad() {
		return version != null;
	}

	@Override
	public void doLoad() {
		if (version != null) {
			application.displayMainWindow(profile.getGameDir(),
					version.getJar());
		}
	}

	@Override
	protected String getLoadingStatus() {
		if (version != null) {
			return "found";
		} else {
			return "failed";
		}
	}

	@Override
	public String getVersionName() {
		return profile.getName();
	}

	@Override
	public String getVersionPrefix() {
		return "local";
	}
}
