package amidst.gui.version;

import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;
import amidst.Amidst;
import amidst.Application;
import amidst.Options;
import amidst.Util;
import amidst.logging.Log;
import amidst.version.LatestVersionList;
import amidst.version.MinecraftProfile;
import amidst.version.VersionFactory;

public class VersionSelectWindow {
	private Application application;
	private VersionFactory versionFactory = new VersionFactory();
	private JFrame frame = new JFrame();

	public VersionSelectWindow(Application application) {
		this.application = application;

		LatestVersionList.get().load(true);

		if (!isValidMinecraftDirectory()) {
			Log.crash("Unable to find Minecraft directory at: "
					+ Util.minecraftDirectory);
			return;
		}

		initFrame();
	}

	private boolean isValidMinecraftDirectory() {
		return Util.minecraftDirectory.exists()
				&& Util.minecraftDirectory.isDirectory();
	}

	private void initFrame() {
		frame = new JFrame("Profile Selector");
		frame.setIconImage(Amidst.icon);
		frame.getContentPane().setLayout(new MigLayout());
		frame.add(createTitleLabel(), "h 20!,w :400:, growx, pushx, wrap");

		VersionSelectPanel versionSelector = createVersionSelectPanel();
		frame.add(new JScrollPane(versionSelector), "grow, push, h 80::");
		frame.addKeyListener(versionSelector);

		frame.pack();
		frame.setLocation(200, 200);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				frame.dispose();
				System.exit(0);
			}
		});
	}

	private JLabel createTitleLabel() {
		final JLabel result = new JLabel("Please select a Minecraft version:",
				SwingConstants.CENTER);
		result.setFont(new Font("arial", Font.BOLD, 16));
		return result;
	}

	private VersionSelectPanel createVersionSelectPanel() {
		VersionSelectPanel versionSelector = new VersionSelectPanel();
		versionSelector.setEmptyMessage("Scanning...");
		startLoadVersionsThread(versionSelector);
		return versionSelector;
	}

	private void startLoadVersionsThread(
			final VersionSelectPanel versionSelector) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				loadVersions(versionSelector);
			}
		}).start();
	}

	private void loadVersions(final VersionSelectPanel versionSelector) {
		versionFactory.scanForProfiles();
		MinecraftProfile[] localVersions = versionFactory.getProfiles();
		String selectedProfile = Options.instance.lastProfile.get();

		if (localVersions == null) {
			versionSelector.setEmptyMessage("Empty");
			return;
		}
		for (int i = 0; i < localVersions.length; i++) {
			versionSelector.addVersion(new LocalVersionComponent(application,
					localVersions[i]));
		}
		versionSelector.addVersion(new RemoteVersionComponent(application));

		if (selectedProfile != null)
			versionSelector.select(selectedProfile);

		frame.pack();
		try {
			Thread.sleep(100);
		} catch (InterruptedException ignored) {
		}
		frame.pack();
	}

	public void dispose() {
		frame.dispose();
	}
}
