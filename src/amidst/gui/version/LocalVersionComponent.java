package amidst.gui.version;

import amidst.Application;
import amidst.Worker;
import amidst.WorkerExecutor;
import amidst.logging.Log;
import amidst.mojangapi.MojangApi;
import amidst.mojangapi.dotminecraft.ProfileDirectory;
import amidst.mojangapi.dotminecraft.VersionDirectory;
import amidst.mojangapi.launcherprofiles.LauncherProfileJson;

public class LocalVersionComponent extends VersionComponent {
	private final Application application;
	private final WorkerExecutor workerExecutor;
	private final MojangApi mojangApi;
	private final LauncherProfileJson profile;

	private volatile VersionDirectory versionDirectory;
	private volatile ProfileDirectory profileDirectory;

	public LocalVersionComponent(Application application,
			WorkerExecutor workerExecutor, MojangApi mojangApi,
			LauncherProfileJson profile) {
		this.application = application;
		this.mojangApi = mojangApi;
		this.workerExecutor = workerExecutor;
		this.profile = profile;
		initComponent();
		initDirectoriesLater();
	}

	private void initDirectoriesLater() {
		workerExecutor.invokeLater(new Worker<Void>() {
			@Override
			public Void execute() {
				doInitDirectories();
				return null;
			}

			@Override
			public void finished(Void result) {
				repaintComponent();
			}
		});
	}

	private void doInitDirectories() {
		this.profileDirectory = createProfileDirectory();
		this.versionDirectory = createVersionDirectory();
	}

	private ProfileDirectory createProfileDirectory() {
		ProfileDirectory result = profile.createProfileDirectory();
		if (result.isValid()) {
			return result;
		} else {
			Log.w("Unable to load profile directory for profile: "
					+ profile.getName());
			return null;
		}
	}

	private VersionDirectory createVersionDirectory() {
		VersionDirectory result = profile.createVersionDirectory(mojangApi);
		if (result != null) {
			return result;
		} else {
			Log.w("Unable to load version directory for profile: "
					+ profile.getName());
			return null;
		}
	}

	@Override
	public boolean isReadyToLoad() {
		return profileDirectory != null && versionDirectory != null;
	}

	@Override
	public void doLoad() {
		if (isReadyToLoad()) {
			mojangApi.set(profileDirectory, versionDirectory);
			application.displayMainWindow();
		}
	}

	@Override
	protected String getLoadingStatus() {
		if (isReadyToLoad()) {
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
