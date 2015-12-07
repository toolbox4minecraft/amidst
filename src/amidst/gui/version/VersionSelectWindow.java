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
import amidst.Worker;
import amidst.WorkerExecutor;
import amidst.logging.Log;
import amidst.mojangapi.dotminecraft.DotMinecraftDirectory;
import amidst.mojangapi.launcherprofiles.LauncherProfileJson;
import amidst.mojangapi.launcherprofiles.LauncherProfilesJson;
import amidst.mojangapi.versionlist.VersionListJson;
import amidst.preferences.StringPreference;

public class VersionSelectWindow {
	private final Application application;
	private final WorkerExecutor workerExecutor;
	private final StringPreference lastProfilePreference;
	private final DotMinecraftDirectory dotMinecraftDirectory;
	private final VersionListJson versionList;

	private final JFrame frame = new JFrame("Profile Selector");
	private final VersionSelectPanel versionSelectPanel;

	public VersionSelectWindow(Application application,
			WorkerExecutor workerExecutor,
			StringPreference lastProfilePreference,
			DotMinecraftDirectory dotMinecraftDirectory,
			VersionListJson versionList) {
		this.application = application;
		this.workerExecutor = workerExecutor;
		this.lastProfilePreference = lastProfilePreference;
		this.dotMinecraftDirectory = dotMinecraftDirectory;
		this.versionList = versionList;
		this.versionSelectPanel = new VersionSelectPanel(lastProfilePreference,
				"Scanning...");
		if (!dotMinecraftDirectory.isValid()) {
			Log.crash("Unable to find minecraft directory at: "
					+ dotMinecraftDirectory.getRoot());
		} else {
			initFrame();
			scanAndLoadVersionsLater();
		}
	}

	private void initFrame() {
		frame.setIconImage(AmidstMetaData.ICON);
		frame.getContentPane().setLayout(new MigLayout());
		frame.add(createTitleLabel(), "h 20!,w :400:, growx, pushx, wrap");
		frame.add(new JScrollPane(versionSelectPanel.getComponent()),
				"grow, push, h 80::");
		frame.pack();
		frame.addKeyListener(versionSelectPanel.getKeyListener());
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				frame.dispose();
				System.exit(0);
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
			launcherProfile = dotMinecraftDirectory.readLauncherProfilesJson();
		} catch (Exception e) {
			Log.crash(e, "Error reading launcher_profiles.json");
		}
		Log.i("Successfully loaded profile list.");
		return launcherProfile;
	}

	private void loadVersions(LauncherProfilesJson launcherProfile) {
		for (LauncherProfileJson profile : launcherProfile.getProfiles()) {
			versionSelectPanel.addVersion(new LocalVersionComponent(
					application, profile, dotMinecraftDirectory, versionList));
		}
		restoreSelection();
		frame.pack();
	}

	private void restoreSelection() {
		String selectedProfile = lastProfilePreference.get();
		if (selectedProfile != null) {
			versionSelectPanel.select(selectedProfile);
		}
	}

	public void dispose() {
		frame.dispose();
	}
}
