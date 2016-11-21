package amidst.gui.profileselect;

import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import amidst.AmidstMetaData;
import amidst.AmidstSettings;
import amidst.Application;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.logging.AmidstLogger;
import amidst.logging.AmidstMessageBox;
import amidst.mojangapi.MojangApi;
import amidst.mojangapi.file.MojangApiParsingException;
import amidst.mojangapi.file.json.launcherprofiles.LauncherProfileJson;
import amidst.mojangapi.file.json.launcherprofiles.LauncherProfilesJson;
import amidst.threading.WorkerExecutor;
import net.miginfocom.swing.MigLayout;

@NotThreadSafe
public class ProfileSelectWindow {
	private static final int HEIGHT_IN_PROFILE_COMPONENTS = 13;

	private final Application application;
	private final AmidstMetaData metadata;
	private final WorkerExecutor workerExecutor;
	private final MojangApi mojangApi;
	private final AmidstSettings settings;

	private final JFrame frame;
	private final ProfileSelectPanel profileSelectPanel;

	@CalledOnlyBy(AmidstThread.EDT)
	public ProfileSelectWindow(
			Application application,
			AmidstMetaData metadata,
			WorkerExecutor workerExecutor,
			MojangApi mojangApi,
			AmidstSettings settings) {
		this.application = application;
		this.metadata = metadata;
		this.workerExecutor = workerExecutor;
		this.mojangApi = mojangApi;
		this.settings = settings;
		this.profileSelectPanel = new ProfileSelectPanel(settings.lastProfile, "Scanning...");
		this.frame = createFrame();
		scanAndLoadProfilesLater();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private JFrame createFrame() {
		JFrame frame = new JFrame("Profile Selector");
		frame.setIconImages(metadata.getIcons());
		frame.getContentPane().setLayout(new MigLayout());
		frame.add(createTitleLabel(), "h 20!,w :400:, growx, pushx, wrap");
		frame.add(createScrollPanel(), getScrollPaneLayoutString());
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
		JLabel result = new JLabel("Please select a Minecraft profile:", SwingConstants.CENTER);
		result.setFont(new Font("arial", Font.BOLD, 16));
		return result;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private JScrollPane createScrollPanel() {
		JScrollPane result = new JScrollPane(profileSelectPanel.getComponent());
		result.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		result.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		return result;
	}

	/**
	 * The preferred width should be at least a scrollbar-width wider than the
	 * ProfileComponent's preferredSize width of 500 (so 520?). The preferred
	 * height should allow the dialog to fit easily on a 720p display, while
	 * being nicely divisible by ProfileComponent's height of 40 (so 520 again
	 * then?).
	 */
	@CalledOnlyBy(AmidstThread.EDT)
	private String getScrollPaneLayoutString() {
		int scrollBarWidth = (Integer) UIManager.get("ScrollBar.width");
		int preferredWidth = ProfileComponent.PREFERRED_WIDTH + scrollBarWidth;
		int preferredHeight = ProfileComponent.PREFERRED_HEIGHT * HEIGHT_IN_PROFILE_COMPONENTS;
		return "grow, push, w :" + preferredWidth + ":, h 80:" + preferredHeight + ":";
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void scanAndLoadProfilesLater() {
		workerExecutor.run(this::scanAndLoadProfiles, this::displayProfiles, this::scanAndLoadProfilesFailed);
	}

	@CalledOnlyBy(AmidstThread.WORKER)
	private LauncherProfilesJson scanAndLoadProfiles() throws MojangApiParsingException, IOException {
		AmidstLogger.info("Scanning for profiles.");
		LauncherProfilesJson launcherProfile = mojangApi.getDotMinecraftDirectory().readLauncherProfilesJson();
		AmidstLogger.info("Successfully loaded profile list.");
		return launcherProfile;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void displayProfiles(LauncherProfilesJson launcherProfile) {
		createProfileComponentsIfNecessary(launcherProfile);
		restoreSelection();
		frame.pack();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void createProfileComponentsIfNecessary(LauncherProfilesJson launcherProfile) {
		if (launcherProfile.getProfiles().isEmpty()) {
			AmidstLogger.warn("No profiles found in launcher_profiles.json");
			profileSelectPanel.setEmptyMessage("No profiles found");
		} else {
			createProfileComponents(launcherProfile);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void createProfileComponents(LauncherProfilesJson launcherProfile) {
		for (LauncherProfileJson profile : launcherProfile.getProfiles()) {
			profileSelectPanel.addProfile(new LocalProfileComponent(application, workerExecutor, mojangApi, profile));
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
	private void scanAndLoadProfilesFailed(Exception e) {
		AmidstLogger.error(e, "Error reading launcher_profiles.json");
		AmidstMessageBox.displayError("Error", e, "Error reading launcher_profiles.json");
		profileSelectPanel.setEmptyMessage("Failed loading");
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void dispose() {
		frame.dispose();
	}
}
