package amidst.gui.profileselect;

import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;
import amidst.AmidstMetaData;
import amidst.AmidstSettings;
import amidst.Application;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.logging.Log;
import amidst.mojangapi.MojangApi;
import amidst.mojangapi.file.MojangApiParsingException;
import amidst.mojangapi.file.json.launcherprofiles.LauncherProfileJson;
import amidst.mojangapi.file.json.launcherprofiles.LauncherProfilesJson;
import amidst.threading.SimpleWorker;
import amidst.threading.WorkerExecutor;

@NotThreadSafe
public class ProfileSelectWindow {
	private final Application application;
	private final AmidstMetaData metadata;
	private final WorkerExecutor workerExecutor;
	private final MojangApi mojangApi;
	private final AmidstSettings settings;

	private final JFrame frame;
	private final ProfileSelectPanel profileSelectPanel;

	@CalledOnlyBy(AmidstThread.EDT)
	public ProfileSelectWindow(Application application,
			AmidstMetaData metadata, WorkerExecutor workerExecutor,
			MojangApi mojangApi, AmidstSettings settings) {
		this.application = application;
		this.metadata = metadata;
		this.workerExecutor = workerExecutor;
		this.mojangApi = mojangApi;
		this.settings = settings;
		this.profileSelectPanel = new ProfileSelectPanel(settings.lastProfile,
				"Scanning...");
		this.frame = createFrame();
		scanAndLoadProfilesLater();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private JFrame createFrame() {
		JFrame frame = new JFrame("Profile Selector");
		frame.setIconImage(metadata.getIcon());
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
	private void scanAndLoadProfilesLater() {
		workerExecutor.invokeLater(new SimpleWorker<LauncherProfilesJson>() {
			@Override
			protected LauncherProfilesJson main()
					throws MojangApiParsingException, IOException {
				return scanAndLoadProfiles();
			}

			@Override
			protected void onMainFinished(LauncherProfilesJson launcherProfile) {
				displayProfiles(launcherProfile);
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
	private LauncherProfilesJson scanAndLoadProfiles()
			throws MojangApiParsingException, IOException {
		Log.i("Scanning for profiles.");
		LauncherProfilesJson launcherProfile = mojangApi
				.getDotMinecraftDirectory().readLauncherProfilesJson();
		Log.i("Successfully loaded profile list.");
		return launcherProfile;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void displayProfiles(LauncherProfilesJson launcherProfile) {
		createProfileComponents(launcherProfile);
		restoreSelection();
		frame.pack();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void createProfileComponents(LauncherProfilesJson launcherProfile) {
		for (LauncherProfileJson profile : launcherProfile.getProfiles()) {
			profileSelectPanel.addProfile(new LocalProfileComponent(
					application, workerExecutor, mojangApi, profile));
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void restoreSelection() {
		String profileName = settings.lastProfile.get();
		if (profileName != null && !profileName.isEmpty()) {
			profileSelectPanel.select(profileName);
		} else {
			profileSelectPanel.selectFirst();
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void dispose() {
		frame.dispose();
	}
}
