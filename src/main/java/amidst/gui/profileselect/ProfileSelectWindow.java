package amidst.gui.profileselect;

import amidst.AmidstMetaData;
import amidst.AmidstSettings;
import amidst.Application;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.logging.AmidstLogger;
import amidst.logging.AmidstMessageBox;
import amidst.mojangapi.LauncherProfileRunner;
import amidst.mojangapi.file.MinecraftInstallation;
import amidst.mojangapi.file.UnresolvedLauncherProfile;
import amidst.mojangapi.file.VersionListProvider;
import amidst.threading.WorkerExecutor;
import amidst.util.SwingUtils;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * A window that shows the profiles that are installed.
 * <p>
 * Some profiles may be modded or higher than the supported
 * Amidst application.
 */
@NotThreadSafe
public class ProfileSelectWindow {

	private static final int HEIGHT_IN_PROFILE_COMPONENTS = 13;

	/**
	 * The window that contains the profile selection list.
	 */
	private final JFrame frame;

	private volatile boolean isDisposed = false;

	@CalledOnlyBy(AmidstThread.EDT)
	public ProfileSelectWindow(
			Application application,
			AmidstMetaData metadata,
			WorkerExecutor workerExecutor,
			VersionListProvider versionListProvider,
			MinecraftInstallation minecraftInstallation,
			LauncherProfileRunner launcherProfileRunner,
			AmidstSettings settings) {

		frame = new JFrame("Profile Selector");
		frame.setIconImages(metadata.getIcons());
		frame.getContentPane().setLayout(new MigLayout());

		JLabel title = new JLabel("Please select a Minecraft profile:", SwingConstants.CENTER);
		title.setFont(new Font("arial", Font.BOLD, 16));
		frame.add(title, "h 20!,w :400:, growx, pushx, wrap");

		ProfileSelectPanel profileSelectPanel = new ProfileSelectPanel(settings.lastProfile, "Scanning...");
		JScrollPane scrollPane = new JScrollPane(profileSelectPanel.getComponent());
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setUnitIncrement(14);

		// The preferred width should be at least a scrollbar-width wider than the
		// ProfileComponent's preferredSize width of 500 (so 520?). The preferred
		// height should allow the dialog to fit easily on a 720p display, while
		// being nicely divisible by ProfileComponent's height of 40 (so 520 again
		// then?).
		int scrollBarWidth = (Integer) UIManager.get("ScrollBar.width");
		int preferredWidth = ProfileComponent.PREFERRED_WIDTH + scrollBarWidth;
		int preferredHeight = ProfileComponent.PREFERRED_HEIGHT * HEIGHT_IN_PROFILE_COMPONENTS;

		frame.add(scrollPane, "grow, push, w :" + preferredWidth + ":, h 80:" + preferredHeight + ":");

		frame.pack();
		frame.addKeyListener(profileSelectPanel.createKeyListener());
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				application.exitGracefully();
			}
		});
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		workerExecutor.run(() -> {
			AmidstLogger.info("Scanning for profiles.");
			List<UnresolvedLauncherProfile> launcherProfiles = minecraftInstallation.readLauncherProfiles();
			AmidstLogger.info("Successfully loaded profile list.");
			return launcherProfiles;
		}, launcherProfiles -> {
			if (launcherProfiles.isEmpty()) {
				AmidstLogger.warn("No profiles found in launcher_profiles.json");
				profileSelectPanel.setEmptyMessage("No profiles found");
			} else {
				launcherProfiles.stream()
						.map(p ->
								new LocalProfileComponent(
										application,
										workerExecutor,
										versionListProvider,
										launcherProfileRunner,
										p
								)
						).forEach(profileSelectPanel::addProfile);
				versionListProvider.onDownloadRemoteFinished(() -> {
					if (!isDisposed) {
						profileSelectPanel.resolveAllLater();
					}
				});
				profileSelectPanel.resolveAllLater();
			}
			String profileName = settings.lastProfile.get();
			if (profileName != null && !profileName.isEmpty()) {
				profileSelectPanel.select(profileName);
			} else {
				profileSelectPanel.selectFirst();
			}
			frame.pack();
		}, e -> {
			AmidstLogger.error(e, "Error reading launcher_profiles.json");
			AmidstMessageBox.displayError("Error", e, "Error reading launcher_profiles.json");
			profileSelectPanel.setEmptyMessage("Failed loading");
		});
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void dispose() {
		isDisposed = true;
		SwingUtils.destroyComponentTree(frame);
	}
}
