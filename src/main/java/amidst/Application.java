package amidst;

import java.util.Optional;

import javax.swing.SwingUtilities;

import amidst.dependency.injection.Factory0;
import amidst.dependency.injection.Factory1;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.gui.license.LicenseWindow;
import amidst.gui.main.MainWindow;
import amidst.gui.main.MainWindowDialogs;
import amidst.gui.main.UpdatePrompt;
import amidst.gui.profileselect.ProfileSelectWindow;
import amidst.mojangapi.LauncherProfileRunner;
import amidst.mojangapi.RunningLauncherProfile;
import amidst.mojangapi.file.LauncherProfile;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceCreationException;

@NotThreadSafe
public class Application {
	private final LauncherProfileRunner launcherProfileRunner;
	private final Factory1<MainWindowDialogs, UpdatePrompt> noisyUpdatePromptFactory;
	private final Factory0<UpdatePrompt> silentUpdatePromptFactory;
	private final Factory1<RunningLauncherProfile, MainWindow> mainWindowFactory;
	private final Factory0<ProfileSelectWindow> profileSelectWindowFactory;
	private final Factory0<LicenseWindow> licenseWindowFactory;

	private volatile ProfileSelectWindow profileSelectWindow;
	private volatile MainWindow mainWindow;
	private volatile Optional<LauncherProfile> selectedLauncherProfile;

	@CalledOnlyBy(AmidstThread.EDT)
	public Application(
			Optional<LauncherProfile> preferredLauncherProfile,
			LauncherProfileRunner launcherProfileRunner,
			Factory1<MainWindowDialogs, UpdatePrompt> noisyUpdatePromptFactory,
			Factory0<UpdatePrompt> silentUpdatePromptFactory,
			Factory1<RunningLauncherProfile, MainWindow> mainWindowFactory,
			Factory0<ProfileSelectWindow> profileSelectWindowFactory,
			Factory0<LicenseWindow> licenseWindowFactory) {
		this.selectedLauncherProfile = preferredLauncherProfile;
		this.launcherProfileRunner = launcherProfileRunner;
		this.noisyUpdatePromptFactory = noisyUpdatePromptFactory;
		this.silentUpdatePromptFactory = silentUpdatePromptFactory;
		this.mainWindowFactory = mainWindowFactory;
		this.profileSelectWindowFactory = profileSelectWindowFactory;
		this.licenseWindowFactory = licenseWindowFactory;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void run() throws MinecraftInterfaceCreationException {
		checkForUpdatesSilently();
		if (selectedLauncherProfile.isPresent()) {
			displayMainWindow(launcherProfileRunner.run(selectedLauncherProfile.get()));
		} else {
			displayProfileSelectWindow();
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void checkForUpdates(MainWindowDialogs dialogs) {
		noisyUpdatePromptFactory.create(dialogs).check();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void checkForUpdatesSilently() {
		silentUpdatePromptFactory.create().check();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public MainWindow displayMainWindow(RunningLauncherProfile runningLauncherProfile) {
		selectedLauncherProfile = Optional.of(runningLauncherProfile.getLauncherProfile());
		setMainWindow(mainWindowFactory.create(runningLauncherProfile));
		setProfileSelectWindow(null);
		return mainWindow;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public ProfileSelectWindow displayProfileSelectWindow() {
		setProfileSelectWindow(profileSelectWindowFactory.create());
		setMainWindow(null);
		return profileSelectWindow;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void setProfileSelectWindow(ProfileSelectWindow profileSelectWindow) {
		disposeProfileSelectWindow();
		this.profileSelectWindow = profileSelectWindow;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void setMainWindow(MainWindow mainWindow) {
		disposeMainWindow();
		this.mainWindow = mainWindow;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void disposeProfileSelectWindow() {
		ProfileSelectWindow profileSelectWindow = this.profileSelectWindow;
		if (profileSelectWindow != null) {
			profileSelectWindow.dispose();
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void disposeMainWindow() {
		MainWindow mainWindow = this.mainWindow;
		if (mainWindow != null) {
			mainWindow.dispose();
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void displayLicenseWindow() {
		licenseWindowFactory.create();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void exitGracefully() {
		dispose();
		System.exit(0);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void dispose() {
		setProfileSelectWindow(null);
		setMainWindow(null);
	}


	@CalledOnlyBy(AmidstThread.EDT)
	public void restart() {
		dispose();
		SwingUtilities.invokeLater(() -> {
			try {
				run();
			} catch(MinecraftInterfaceCreationException e) {
				throw new RuntimeException("Unexpected exception while restarting Amidst", e);
			}
		});
	}
}
