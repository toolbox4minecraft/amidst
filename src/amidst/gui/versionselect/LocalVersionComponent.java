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
import amidst.mojangapi.minecraftinterface.local.LocalMinecraftInterfaceBuilder.LocalMinecraftInterfaceCreationException;
import amidst.threading.ExceptionalWorker;
import amidst.threading.Worker;
import amidst.threading.WorkerExecutor;

@NotThreadSafe
public class LocalVersionComponent extends VersionComponent {
	private final Application application;
	private final WorkerExecutor workerExecutor;
	private final MojangApi mojangApi;
	private final LauncherProfileJson profile;

	private volatile boolean isReadyToLoad = false;
	private volatile boolean isLoading = false;
	private volatile boolean failedLoading = false;
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
		workerExecutor.invokeLater(new Worker<Boolean>() {
			@CalledOnlyBy(AmidstThread.WORKER)
			@Override
			public Boolean execute() {
				profileDirectory = createProfileDirectory();
				versionDirectory = createVersionDirectory();
				return profileDirectory != null && versionDirectory != null;
			}

			@CalledOnlyBy(AmidstThread.EDT)
			@Override
			public void finished(Boolean result) {
				isReadyToLoad = result;
				repaintComponent();
			}
		});
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
	public void load() {
		isLoading = true;
		repaintComponent();
		workerExecutor.invokeLater(new ExceptionalWorker<Void>() {
			@CalledOnlyBy(AmidstThread.WORKER)
			@Override
			public Void execute()
					throws LocalMinecraftInterfaceCreationException {
				mojangApi.set(profileDirectory, versionDirectory);
				return null;
			}

			@CalledOnlyBy(AmidstThread.EDT)
			@Override
			public void error(Exception e) {
				Log.e(e.getMessage());
				e.printStackTrace();
				isLoading = false;
				failedLoading = true;
				repaintComponent();
			}

			@CalledOnlyBy(AmidstThread.EDT)
			@Override
			public void finished(Void result) {
				isLoading = false;
				repaintComponent();
				application.displayMainWindow();
			}
		});
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	public boolean isReadyToLoad() {
		return isReadyToLoad;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	public boolean isLoading() {
		return isLoading;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	public boolean failedLoading() {
		return failedLoading;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	public String getProfileName() {
		return profile.getName();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	public String getVersionName() {
		if (isReadyToLoad) {
			return versionDirectory.getVersionId();
		} else {
			return "";
		}
	}
}
