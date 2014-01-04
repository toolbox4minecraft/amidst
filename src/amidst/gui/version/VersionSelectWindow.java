package amidst.gui.version;

import java.awt.Container;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import net.miginfocom.swing.MigLayout;
import amidst.Amidst;
import amidst.Options;
import amidst.Util;
import amidst.logging.Log;
import amidst.version.LatestVersionList;
import amidst.version.MinecraftProfile;
import amidst.version.VersionFactory;

public class VersionSelectWindow extends JFrame {
	private static VersionSelectWindow instance;
	private VersionFactory versionFactory = new VersionFactory();
	
	public VersionSelectWindow() {
		super("Profile Selector");
		instance = this;
		setIconImage(Amidst.icon);
		Container contentPane = getContentPane();
		contentPane.setLayout(new MigLayout());
		
		LatestVersionList.get().load(true);
		
		if (!Util.minecraftDirectory.exists() || !Util.minecraftDirectory.isDirectory()) {
			Log.crash("Unable to find Minecraft directory at: " + Util.minecraftDirectory);
			return;
		}
		
		final JLabel titleLabel = new JLabel("Please select a Minecraft version:", JLabel.CENTER);
		titleLabel.setFont(new Font("arial", Font.BOLD, 16));
		
		add(titleLabel, "h 20!,w :400:, growx, pushx, wrap");

		final VersionSelectPanel versionSelector = new VersionSelectPanel();
		
		(new Thread(new Runnable() {
			@Override
			public void run() {
				versionFactory.scanForProfiles();
				MinecraftProfile[] localVersions = versionFactory.getProfiles();
				String selectedProfile = Options.instance.lastProfile.get();
				
				if (localVersions == null) {
					versionSelector.setEmptyMessage("Empty");
					return;
				}
				for (int i = 0; i < localVersions.length; i++) {
					versionSelector.addVersion(new VersionComponent(localVersions[i]));
					if ((selectedProfile != null) && localVersions[i].getProfileName().equals(selectedProfile))
						versionSelector.select(i);
				}
				pack();
			}
		})).start();
		
		versionSelector.setEmptyMessage("Scanning...");
		
		JScrollPane scrollPane = new JScrollPane(versionSelector);
		add(scrollPane, "grow, push, h 80::");
		pack();
		setLocation(200, 200);
		setVisible(true);
		
		addKeyListener(versionSelector);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
				System.exit(0);
			}
		});
	}
	
	public static VersionSelectWindow get() {
		return instance;
	}
}
