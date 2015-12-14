package amidst.gui.profileselect;

import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;

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
import amidst.documentation.NotThreadSafe;
import amidst.logging.Log;
import amidst.mojangapi.MojangApi;
import amidst.mojangapi.file.json.launcherprofiles.LauncherProfileJson;
import amidst.mojangapi.file.json.launcherprofiles.LauncherProfilesJson;
import amidst.threading.SimpleWorker;
import amidst.threading.WorkerExecutor;

@NotThreadSafe
public class ProfileSelectWindow {
	private final Application application;
	private final WorkerExecutor workerExecutor;
	private final MojangApi mojangApi;
	private final Settings settings;

	private final JFrame frame;
	private final ProfileSelectPanel profileSelectPanel;

	@CalledOnlyBy(AmidstThread.EDT)
	public ProfileSelectWindow(Application application,
			WorkerExecutor workerExecutor, MojangApi mojangApi,
			Settings settings) {
		this.application = application;
		this.workerExecutor = workerExecutor;
		this.mojangApi = mojangApi;
		this.settings = settings;
		this.profileSelectPanel = new ProfileSelectPanel(settings.lastProfile,
				"Scanning...");
		this.frame = createFrame();
		scanAndLoadVersionsLater();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private JFrame createFrame() {
		JFrame frame = new JFrame("Profile Selector");
		frame.setIconImage(AmidstMetaData.ICON);
		frame.getContentPane().setLayout(new MigLayout());
		frame.add(createTitleLabel(), "h 20!,w :400:, growx, pushx, wrap");
		frame.add(new JScrollPane(profileSelectPanel.getComponent()),
				"grow, push, h 80::");
		frame.pack();
		frame.addKeyListener(profileSelectPanel.createKeyListener());
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				application.exitGracefully();
			}
		});
		frame.setLocation(200, 200);
		frame.setVisible(true);
		return frame;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private JLabel createTitleLabel() {
		JLabel result = new JLabel("Please select a Minecraft profile:",
				SwingConstants.CENTER);
		result.setFont(new Font("arial", Font.BOLD, 16));
		return result;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void scanAndLoadVersionsLater() {
		workerExecutor.invokeLater(new SimpleWorker<LauncherProfilesJson>() {
			@Override
			protected LauncherProfilesJson main() throws FileNotFoundException {
				return scanAndLoadVersions();
			}

			@Override
			protected void onMainFinished(LauncherProfilesJson launcherProfile) {
				loadVersions(launcherProfile);
			}

			@Override
			protected void onMainFinishedWithException(Exception e) {
				Log.e("Error reading launcher_profiles.json");
				e.printStackTrace();
				profileSelectPanel.setEmptyMessage("Failed loading");
			}
		});
	}

	@CalledOnlyBy(AmidstThread.WORKER)
	private LauncherProfilesJson scanAndLoadVersions()
			throws FileNotFoundException {
		Log.i("Scanning for profiles.");
		LauncherProfilesJson launcherProfile = mojangApi
				.getDotMinecraftDirectory().readLauncherProfilesJson();
		Log.i("Successfully loaded profile list.");
		return launcherProfile;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void loadVersions(LauncherProfilesJson launcherProfile) {
		createVersionComponents(launcherProfile);
		restoreSelection();
		frame.pack();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void createVersionComponents(LauncherProfilesJson launcherProfile) {
		for (LauncherProfileJson profile : launcherProfile.getProfiles()) {
			profileSelectPanel.addVersion(new LocalProfileComponent(
					application, workerExecutor, mojangApi, profile));
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void restoreSelection() {
		String profileName = settings.lastProfile.get();
		if (profileName != null) {
			profileSelectPanel.select(profileName);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void dispose() {
		frame.dispose();
	}
}
