package amidst.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import MoF.Project;
import MoF.UpdateManager;
import amidst.Amidst;
import amidst.Application;
import amidst.gui.menu.AmidstMenu;

public class MapWindow {
	private static MapWindow instance;

	public static MapWindow getInstance() {
		return instance;
	}

	private Application application;

	private JFrame frame = new JFrame();
	private AmidstMenu menuBar;
	private Container contentPane;
	private Project project;

	private SeedPrompt seedPrompt = new SeedPrompt(frame);

	public MapWindow(Application application) {
		this.application = application;
		frame.setTitle("Amidst v" + Amidst.version());
		frame.setSize(1000, 800);
		frame.setIconImage(Amidst.icon);
		initContentPane();
		initUpdateManager();
		initMenuBar();
		initCloseListener();
		instance = this;
		frame.setVisible(true);
	}

	private void initContentPane() {
		contentPane = frame.getContentPane();
		contentPane.setLayout(new BorderLayout());
	}

	private void initUpdateManager() {
		new UpdateManager(frame, true).start();
	}

	private void initMenuBar() {
		menuBar = new AmidstMenu(application);
		frame.setJMenuBar(menuBar.getMenuBar());
	}

	private void initCloseListener() {
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				frame.dispose();
				System.exit(0);
			}
		});
	}

	private void clearProject() {
		// TODO: Release resources
		if (project != null) {
			frame.removeKeyListener(project.getKeyListener());
			project.dispose();
			contentPane.remove(project.getPanel());
		}
	}

	public void setProject(Project project) {
		clearProject();

		this.project = project;
		menuBar.setMapMenuEnabled(true);
		frame.addKeyListener(project.getKeyListener());
		contentPane.add(this.project.getPanel(), BorderLayout.CENTER);
		frame.validate();
	}

	public Project getProject() {
		return project;
	}

	@Deprecated
	public JFrame getFrame() {
		return frame;
	}

	public void dispose() {
		frame.dispose();
	}

	public String askForSeed() {
		return seedPrompt.askForSeed();
	}
}
