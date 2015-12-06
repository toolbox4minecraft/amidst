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
import amidst.minecraft.LocalMinecraftInstallation;
import amidst.preferences.StringPreference;
import amidst.version.MinecraftProfile;
import amidst.version.VersionFactory;

public class VersionSelectWindow {
	private final Application application;
	private final WorkerExecutor workerExecutor;
	private final StringPreference lastProfilePreference;

	private final VersionFactory versionFactory = new VersionFactory();

	private final JFrame frame = new JFrame("Profile Selector");
	private final VersionSelectPanel versionSelectPanel;

	public VersionSelectWindow(Application application,
			WorkerExecutor workerExecutor,
			StringPreference lastProfilePreference) {
		this.application = application;
		this.workerExecutor = workerExecutor;
		this.lastProfilePreference = lastProfilePreference;
		this.versionSelectPanel = new VersionSelectPanel(lastProfilePreference,
				"Scanning...");
		if (!LocalMinecraftInstallation.getDotMinecraftDirectory().isValid()) {
			Log.crash("Unable to find minecraft directory at: "
					+ LocalMinecraftInstallation.getDotMinecraftDirectory()
							.getRoot());
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
		workerExecutor.invokeLater(new Worker<MinecraftProfile[]>() {
			@Override
			public MinecraftProfile[] execute() {
				return scanAndLoadVersions();
			}

			@Override
			public void finished(MinecraftProfile[] profiles) {
				loadVersions(profiles);
			}
		});
	}

	private MinecraftProfile[] scanAndLoadVersions() {
		versionFactory.scanForProfiles();
		return versionFactory.getProfiles();
	}

	private void loadVersions(MinecraftProfile[] profiles) {
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
