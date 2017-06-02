package amidst.gui.profileselect;

import java.io.IOException;
import java.util.Optional;

import amidst.Application;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.logging.AmidstLogger;
import amidst.logging.AmidstMessageBox;
import amidst.mojangapi.LauncherProfileRunner;
import amidst.mojangapi.RunningLauncherProfile;
import amidst.mojangapi.file.LauncherProfile;
import amidst.mojangapi.file.UnresolvedLauncherProfile;
import amidst.mojangapi.file.VersionListProvider;
import amidst.mojangapi.minecraftinterface.local.LocalMinecraftInterfaceCreationException;
import amidst.parsing.FormatException;
import amidst.threading.WorkerExecutor;

@NotThreadSafe
public class LocalProfileComponent extends ProfileComponent {
	private final Application application;
	private final WorkerExecutor workerExecutor;
	private final VersionListProvider versionListProvider;
	private final LauncherProfileRunner launcherProfileRunner;
	private final UnresolvedLauncherProfile unresolvedProfile;

	private volatile boolean isResolving = false;
	private volatile boolean failedResolving = false;
	private volatile boolean isLoading = false;
	private volatile boolean failedLoading = false;
	private volatile LauncherProfile resolvedProfile;

	@CalledOnlyBy(AmidstThread.EDT)
	public LocalProfileComponent(
			Application application,
			WorkerExecutor workerExecutor,
			VersionListProvider versionListProvider,
			LauncherProfileRunner launcherProfileRunner,
			UnresolvedLauncherProfile unresolvedProfile) {
		this.application = application;
		this.workerExecutor = workerExecutor;
		this.versionListProvider = versionListProvider;
		this.launcherProfileRunner = launcherProfileRunner;
		this.unresolvedProfile = unresolvedProfile;
		initComponent();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	public void resolveLater() {
		resolvedProfile = null;
		isResolving = true;
		repaintComponent();
		workerExecutor.run(this::tryResolve, this::resolveFinished);
	}

	@CalledOnlyBy(AmidstThread.WORKER)
	private Optional<LauncherProfile> tryResolve() {
		try {
			return Optional.of(unresolvedProfile.resolveToVanilla(versionListProvider.getRemoteOrElseLocal()));
		} catch (FormatException | IOException e) {
			AmidstLogger.warn(e);
			return Optional.empty();
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void resolveFinished(Optional<LauncherProfile> launcherProfile) {
		isResolving = false;
		failedResolving = !launcherProfile.isPresent();
		resolvedProfile = launcherProfile.orElse(null);
		repaintComponent();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	public void load() {
		isLoading = true;
		repaintComponent();
		displayModdedMinecraftInfoIfNecessary();
		workerExecutor.run(this::tryLoad, this::loadFinished);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void displayModdedMinecraftInfoIfNecessary() {
		if (!resolvedProfile.isVersionListedInProfile()) {
			String message = "Amidst does not support modded Minecraft versions! Using underlying vanilla Minecraft version "
					+ resolvedProfile.getVersionId() + " instead.";
			AmidstLogger.info(message);
			AmidstMessageBox.displayInfo("Info", message);
		}
	}

	@CalledOnlyBy(AmidstThread.WORKER)
	private Optional<RunningLauncherProfile> tryLoad() {
		try {
			AmidstLogger.info(
					"using minecraft launcher profile '" + resolvedProfile.getProfileName() + "' with versionId '"
							+ resolvedProfile.getVersionName() + "'");
			return Optional.of(launcherProfileRunner.run(resolvedProfile));
		} catch (LocalMinecraftInterfaceCreationException e) {
			AmidstLogger.error(e);
			AmidstMessageBox.displayError("Error", e);
			return Optional.empty();
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void loadFinished(Optional<RunningLauncherProfile> runningLauncherProfile) {
		isLoading = false;
		failedLoading = !runningLauncherProfile.isPresent();
		repaintComponent();
		if (runningLauncherProfile.isPresent()) {
			application.displayMainWindow(runningLauncherProfile.get());
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected boolean isResolving() {
		return isResolving;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected boolean failedResolving() {
		return failedResolving;
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
		return resolvedProfile != null;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected String getProfileName() {
		return unresolvedProfile.getName();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected String getVersionName() {
		if (resolvedProfile != null) {
			return resolvedProfile.getVersionName();
		} else {
			return "";
		}
	}
}
