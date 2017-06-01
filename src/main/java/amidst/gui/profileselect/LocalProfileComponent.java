package amidst.gui.profileselect;

import java.io.IOException;
import java.util.Locale;
import java.util.regex.Pattern;

import amidst.Application;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.logging.AmidstLogger;
import amidst.logging.AmidstMessageBox;
import amidst.mojangapi.MojangApi;
import amidst.mojangapi.file.LauncherProfile;
import amidst.mojangapi.file.MojangApiParsingException;
import amidst.mojangapi.file.UnresolvedLauncherProfile;
import amidst.mojangapi.minecraftinterface.local.LocalMinecraftInterfaceCreationException;
import amidst.threading.WorkerExecutor;

@NotThreadSafe
public class LocalProfileComponent extends ProfileComponent {
	private final Application application;
	private final WorkerExecutor workerExecutor;
	private final MojangApi mojangApi;
	private final UnresolvedLauncherProfile unresolvedProfile;

	private volatile boolean isSearching = false;
	private volatile boolean failedSearching = false;
	private volatile boolean isLoading = false;
	private volatile boolean failedLoading = false;
	private volatile LauncherProfile resolvedProfile;

	@CalledOnlyBy(AmidstThread.EDT)
	public LocalProfileComponent(
			Application application,
			WorkerExecutor workerExecutor,
			MojangApi mojangApi,
			UnresolvedLauncherProfile unresolvedProfile) {
		this.application = application;
		this.mojangApi = mojangApi;
		this.workerExecutor = workerExecutor;
		this.unresolvedProfile = unresolvedProfile;
		initComponent();
		initDirectoriesLater();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void initDirectoriesLater() {
		isSearching = true;
		repaintComponent();
		workerExecutor.run(this::tryFind, this::findFinished);
	}

	@CalledOnlyBy(AmidstThread.WORKER)
	private boolean tryFind() {
		try {
			resolvedProfile = unresolvedProfile.resolve(mojangApi.getVersionList());
			return true;
		} catch (IOException | MojangApiParsingException e) {
			AmidstLogger.warn(e);
			return false;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void findFinished(boolean isSuccessful) {
		isSearching = false;
		failedSearching = !isSuccessful;
		repaintComponent();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	public void load() {
		isLoading = true;
		repaintComponent();
		workerExecutor.run(this::tryLoad, this::loadFinished);
	}

	@CalledOnlyBy(AmidstThread.WORKER)
	private boolean tryLoad() {
		// TODO: Replace with proper handling for modded profiles.
		try {
			AmidstLogger.info(
					"using minecraft launcher profile '" + getProfileName() + "' with versionId '" + getVersionName()
							+ "'");

			String possibleModProfiles = ".*(optifine|forge).*";
			if (Pattern.matches(possibleModProfiles, getVersionName().toLowerCase(Locale.ENGLISH))) {
				AmidstLogger.error(
						"Amidst does not support modded Minecraft profiles! Please select or create an unmodded Minecraft profile via the Minecraft Launcher.");
				AmidstMessageBox.displayError(
						"Error",
						"Amidst does not support modded Minecraft profiles! Please select or create an unmodded Minecraft profile via the Minecraft Launcher.");
				return false;
			}

			mojangApi.setLauncherProfile(resolvedProfile);
			return true;
		} catch (LocalMinecraftInterfaceCreationException e) {
			AmidstLogger.error(e);
			AmidstMessageBox.displayError("Error", e);
			return false;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void loadFinished(boolean isSuccessful) {
		isLoading = false;
		failedLoading = !isSuccessful;
		repaintComponent();
		if (isSuccessful) {
			application.displayMainWindow();
		}
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
		return unresolvedProfile.getName();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected String getVersionName() {
		if (isReadyToLoad()) {
			return resolvedProfile.getVersionId();
		} else {
			return "";
		}
	}
}
