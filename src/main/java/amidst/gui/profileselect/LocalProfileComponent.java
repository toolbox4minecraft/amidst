package amidst.gui.profileselect;

import java.io.FileNotFoundException;

import amidst.Application;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.logging.Log;
import amidst.mojangapi.MojangApi;
import amidst.mojangapi.file.directory.ProfileDirectory;
import amidst.mojangapi.file.directory.VersionDirectory;
import amidst.mojangapi.file.json.launcherprofiles.LauncherProfileJson;
import amidst.mojangapi.minecraftinterface.local.LocalMinecraftInterfaceCreationException;
import amidst.threading.SimpleWorker;
import amidst.threading.SimpleWorkerWithoutResult;
import amidst.threading.WorkerExecutor;

@NotThreadSafe
public class LocalProfileComponent extends ProfileComponent {
	private final Application application;
	private final WorkerExecutor workerExecutor;
	private final MojangApi mojangApi;
	private final LauncherProfileJson profile;

	private volatile boolean isSearching = false;
	private volatile boolean failedSearching = false;
	private volatile boolean isLoading = false;
	private volatile boolean failedLoading = false;
	private volatile VersionDirectory versionDirectory;
	private volatile ProfileDirectory profileDirectory;

	@CalledOnlyBy(AmidstThread.EDT)
	public LocalProfileComponent(Application application,
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
		isSearching = true;
		repaintComponent();
		workerExecutor.invokeLater(new SimpleWorker<Boolean>() {
			@Override
			protected Boolean main() {
				try {
					profileDirectory = profile
							.createValidProfileDirectory(mojangApi);
					versionDirectory = profile
							.createValidVersionDirectory(mojangApi);
					return true;
				} catch (FileNotFoundException e) {
					Log.w(e.getMessage());
					return false;
				}
			}

			@Override
			protected void onMainFinished(Boolean result) {
				isSearching = false;
				failedSearching = !result;
				repaintComponent();
			}
		});
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	public void load() {
		isLoading = true;
		repaintComponent();
		workerExecutor.invokeLater(new SimpleWorkerWithoutResult() {
			@Override
			protected void main()
					throws LocalMinecraftInterfaceCreationException {
				mojangApi.set(profile.getName(), profileDirectory,
						versionDirectory);
			}

			@Override
			protected void onMainFinished() {
				isLoading = false;
				repaintComponent();
				application.displayMainWindow();
			}

			@Override
			protected void onMainFinishedWithException(Exception e) {
				Log.e(e.getMessage());
				e.printStackTrace();
				isLoading = false;
				failedLoading = true;
				repaintComponent();
			}
		});
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected boolean isSearching() {
		return isSearching;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected boolean failedSearching() {
		return failedSearching;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected boolean isLoading() {
		return isLoading;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected boolean failedLoading() {
		return failedLoading;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected boolean isReadyToLoad() {
		return !isSearching && !failedSearching;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected String getProfileName() {
		return profile.getName();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected String getVersionName() {
		if (isReadyToLoad()) {
			return versionDirectory.getVersionId();
		} else {
			return "";
		}
	}
}
