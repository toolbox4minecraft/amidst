package amidst.gui.version;

import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;
import amidst.AmidstMetaData;
import amidst.Application;
import amidst.Settings;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.logging.Log;
import amidst.mojangapi.MojangApi;
import amidst.mojangapi.file.json.launcherprofiles.LauncherProfileJson;
import amidst.mojangapi.file.json.launcherprofiles.LauncherProfilesJson;
import amidst.threading.Worker;
import amidst.threading.WorkerExecutor;

public class VersionSelectWindow {
	private final Application application;
	private final WorkerExecutor workerExecutor;
	private final MojangApi mojangApi;
	private final Settings settings;

	private final JFrame frame = new JFrame("Profile Selector");
	private final VersionSelectPanel versionSelectPanel;

	public VersionSelectWindow(Application application,
			WorkerExecutor workerExecutor, MojangApi mojangApi,
			Settings settings) {
		this.application = application;
		this.workerExecutor = workerExecutor;
		this.mojangApi = mojangApi;
		this.settings = settings;
		this.versionSelectPanel = new VersionSelectPanel(settings.lastProfile,
				"Scanning...");
		initFrame();
		scanAndLoadVersionsLater();
	}

	private void initFrame() {
		frame.setIconImage(AmidstMetaData.ICON);
		frame.getContentPane().setLayout(new MigLayout());
		frame.add(createTitleLabel(), "h 20!,w :400:, growx, pushx, wrap");
		frame.add(new JScrollPane(versionSelectPanel.getComponent()),
				"grow, push, h 80::");
		frame.pack();
		frame.addKeyListener(versionSelectPanel.createKeyListener());
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				application.exitGracefully();
			}
		});
		frame.setLocation(200, 200);
		frame.setVisible(true);
	}

	private JLabel createTitleLabel() {
		final JLabel result = new JLabel("Please select a Minecraft version:",
				SwingConstants.CENTER);
		result.setFont(new Font("arial", Font.BOLD, 16));
		return result;
	}

	private void scanAndLoadVersionsLater() {
		workerExecutor.invokeLater(new Worker<LauncherProfilesJson>() {
			@Override
			public LauncherProfilesJson execute() {
				return scanAndLoadVersions();
			}

			@Override
			public void finished(LauncherProfilesJson launcherProfile) {
				loadVersions(launcherProfile);
			}
		});
	}

	private LauncherProfilesJson scanAndLoadVersions() {
		Log.i("Scanning for profiles.");
		LauncherProfilesJson launcherProfile = null;
		try {
			launcherProfile = mojangApi.getDotMinecraftDirectory()
					.readLauncherProfilesJson();
		} catch (Exception e) {
			Log.crash(e, "Error reading launcher_profiles.json");
		}
		Log.i("Successfully loaded profile list.");
		return launcherProfile;
	}

	private void loadVersions(LauncherProfilesJson launcherProfile) {
		createVersionComponents(launcherProfile);
		restoreSelection();
		frame.pack();
	}

	private void createVersionComponents(LauncherProfilesJson launcherProfile) {
		for (LauncherProfileJson profile : launcherProfile.getProfiles()) {
			versionSelectPanel.addVersion(new LocalVersionComponent(
					application, workerExecutor, mojangApi, profile));
		}
	}

	private void restoreSelection() {
		String profileName = settings.lastProfile.get();
		if (profileName != null) {
			versionSelectPanel.select(profileName);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void dispose() {
		frame.dispose();
	}
}
