package amidst;

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
import amidst.mojangapi.MojangApi;

@NotThreadSafe
public class Application {
	private final MojangApi mojangApi;
	private final Factory1<MainWindowDialogs, UpdatePrompt> noisyUpdatePromptFactory;
	private final Factory0<UpdatePrompt> silentUpdatePromptFactory;
	private final Factory0<MainWindow> mainWindowFactory;
	private final Factory0<ProfileSelectWindow> profileSelectWindowFactory;
	private final Factory0<LicenseWindow> licenseWindowFactory;

	private volatile ProfileSelectWindow profileSelectWindow;
	private volatile MainWindow mainWindow;

	@CalledOnlyBy(AmidstThread.EDT)
	public Application(
			MojangApi mojangApi,
			Factory1<MainWindowDialogs, UpdatePrompt> noisyUpdatePromptFactory,
			Factory0<UpdatePrompt> silentUpdatePromptFactory,
			Factory0<MainWindow> mainWindowFactory,
			Factory0<ProfileSelectWindow> profileSelectWindowFactory,
			Factory0<LicenseWindow> licenseWindowFactory) {
		this.mojangApi = mojangApi;
		this.noisyUpdatePromptFactory = noisyUpdatePromptFactory;
		this.silentUpdatePromptFactory = silentUpdatePromptFactory;
		this.mainWindowFactory = mainWindowFactory;
		this.profileSelectWindowFactory = profileSelectWindowFactory;
		this.licenseWindowFactory = licenseWindowFactory;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void run() {
		checkForUpdatesSilently();
		if (mojangApi.canCreateWorld()) {
			displayMainWindow();
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
	public void displayMainWindow() {
		setMainWindow(mainWindowFactory.create());
		setProfileSelectWindow(null);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void displayProfileSelectWindow() {
		setProfileSelectWindow(profileSelectWindowFactory.create());
		setMainWindow(null);
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
}
