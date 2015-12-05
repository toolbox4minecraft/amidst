package amidst.gui.version;

import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;
import amidst.AmidstMetaData;
import amidst.Application;
import amidst.LongRunningIOExecutor;
import amidst.logging.Log;
import amidst.minecraft.LocalMinecraftInstallation;
import amidst.preferences.StringPreference;
import amidst.version.LatestVersionList;
import amidst.version.MinecraftProfile;
import amidst.version.VersionFactory;

public class VersionSelectWindow {
	private final Application application;
	private final LongRunningIOExecutor longRunningIOExecutor;
	private final StringPreference lastProfilePreference;

	private final VersionFactory versionFactory = new VersionFactory();

	private final JFrame frame = new JFrame("Profile Selector");
	private final VersionSelectPanel versionSelectPanel;

	public VersionSelectWindow(Application application,
			LongRunningIOExecutor longRunningIOExecutor,
			StringPreference lastProfilePreference) {
		this.application = application;
		this.longRunningIOExecutor = longRunningIOExecutor;
		this.lastProfilePreference = lastProfilePreference;
		this.versionSelectPanel = new VersionSelectPanel(lastProfilePreference,
				"Scanning...");
		loadLatestVersionListLater();
		if (!LocalMinecraftInstallation.getMinecraftDirectory().isDirectory()) {
			Log.crash("Unable to find Minecraft directory at: "
					+ LocalMinecraftInstallation.getMinecraftDirectory());
		} else {
			initFrame();
			scanAndLoadVersionsLater();
		}
	}

	private void loadLatestVersionListLater() {
		longRunningIOExecutor.invoke(new Runnable() {
			@Override
			public void run() {
				loadLatestVersionListImmediately();
			}
		});
	}

	private void loadLatestVersionListImmediately() {
		LatestVersionList.get().load();
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
		longRunningIOExecutor.invoke(new Runnable() {
			@Override
			public void run() {
				scanAndLoadVersionsImmediately();
			}
		});
	}

	private void scanAndLoadVersionsImmediately() {
		versionFactory.scanForProfiles();
		loadVersionsLater(versionFactory.getProfiles());
	}

	private void loadVersionsLater(final MinecraftProfile[] profiles) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				loadVersionsImmediately(profiles);
			}
		});
	}

	private void loadVersionsImmediately(MinecraftProfile[] profiles) {
		if (profiles == null) {
			versionSelectPanel.setEmptyMessage("Empty");
		} else {
			addVersions(profiles);
			restoreSelection();
			frame.pack();
		}
	}

	private void addVersions(MinecraftProfile[] profiles) {
		for (MinecraftProfile localVersion : profiles) {
			versionSelectPanel.addVersion(new LocalVersionComponent(
					application, localVersion));
		}
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
