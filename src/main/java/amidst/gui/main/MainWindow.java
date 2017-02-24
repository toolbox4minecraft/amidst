package amidst.gui.main;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import amidst.AmidstMetaData;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.gui.main.menu.AmidstMenu;
import amidst.gui.seedsearcher.SeedSearcherWindow;

@NotThreadSafe
public class MainWindow {
	private final JFrame frame;
	private final WorldSwitcher worldSwitcher;
	private final SeedSearcherWindow seedSearcherWindow;

	@CalledOnlyBy(AmidstThread.EDT)
	public MainWindow(JFrame frame, WorldSwitcher worldSwitcher, SeedSearcherWindow seedSearcherWindow) {
		this.frame = frame;
		this.worldSwitcher = worldSwitcher;
		this.seedSearcherWindow = seedSearcherWindow;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void initializeFrame(AmidstMetaData metadata, String versionString, Actions actions, AmidstMenu menuBar) {
		frame.setSize(1000, 800);
		frame.setIconImages(metadata.getIcons());
		frame.setTitle(versionString);
		frame.setJMenuBar(menuBar.getMenuBar());
		frame.getContentPane().setLayout(new BorderLayout());
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				actions.exit();
			}
		});
		frame.setVisible(true);
		worldSwitcher.clearWorld();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void dispose() {
		worldSwitcher.clearWorld();
		seedSearcherWindow.dispose();
		frame.dispose();
	}
}
