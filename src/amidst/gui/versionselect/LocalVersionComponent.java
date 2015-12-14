package amidst.gui.versionselect;

import amidst.Application;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.logging.Log;
import amidst.mojangapi.MojangApi;
import amidst.mojangapi.file.directory.ProfileDirectory;
import amidst.mojangapi.file.directory.VersionDirectory;
import amidst.mojangapi.file.json.launcherprofiles.LauncherProfileJson;
import amidst.threading.Worker;
import amidst.threading.WorkerExecutor;

@NotThreadSafe
public class LocalVersionComponent extends VersionComponent {
	private final Application application;
	private final WorkerExecutor workerExecutor;
	private final MojangApi mojangApi;
	private final LauncherProfileJson profile;

	private volatile VersionDirectory versionDirectory;
	private volatile ProfileDirectory profileDirectory;

	@CalledOnlyBy(AmidstThread.EDT)
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

	@CalledOnlyBy(AmidstThread.EDT)
	private void initDirectoriesLater() {
		workerExecutor.invokeLater(new Worker<Void>() {
			@CalledOnlyBy(AmidstThread.WORKER)
			@Override
			public Void execute() {
				doInitDirectories();
				return null;
			}

			@CalledOnlyBy(AmidstThread.EDT)
			@Override
			public void finished(Void result) {
				repaintComponent();
			}
		});
	}

	@CalledOnlyBy(AmidstThread.WORKER)
	private void doInitDirectories() {
		this.profileDirectory = createProfileDirectory();
		this.versionDirectory = createVersionDirectory();
	}

	@CalledOnlyBy(AmidstThread.WORKER)
	private ProfileDirectory createProfileDirectory() {
		ProfileDirectory result = profile.createValidProfileDirectory();
		if (result == null) {
			Log.w("Unable to load profile directory for profile: "
					+ profile.getName());
		}
		return result;
	}

	@CalledOnlyBy(AmidstThread.WORKER)
	private VersionDirectory createVersionDirectory() {
		VersionDirectory result = profile
				.createValidVersionDirectory(mojangApi);
		if (result == null) {
			Log.w("Unable to load version directory for profile: "
					+ profile.getName());
		}
		return result;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	public boolean isReadyToLoad() {
		return profileDirectory != null && versionDirectory != null;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	public void doLoad() {
		workerExecutor.invokeLater(new Worker<Void>() {
			@CalledOnlyBy(AmidstThread.WORKER)
			@Override
			public Void execute() {
				mojangApi.set(profileDirectory, versionDirectory);
				return null;
			}

			@CalledOnlyBy(AmidstThread.EDT)
			@Override
			public void finished(Void result) {
				application.displayMainWindow();
			}
		});
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected String getLoadingStatus() {
		if (isReadyToLoad()) {
			return "found";
		} else {
			return "failed";
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	public String getProfileName() {
		return profile.getName();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	public String getVersionName() {
		if (isReadyToLoad()) {
			return versionDirectory.getVersionId();
		} else {
			return "";
		}
	}
}
