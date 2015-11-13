package MoF;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import amidst.Amidst;
import amidst.gui.menu.AmidstMenu;

public class FinderWindow {
	private static FinderWindow instance;

	public static FinderWindow getInstance() {
		return instance;
	}

	private JFrame frame = new JFrame();
	private Container contentPane;
	private Project project;
	private AmidstMenu menuBar;

	public FinderWindow() {
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
		menuBar = new AmidstMenu(this);
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

	public void clearProject() {
		// TODO: Release resources
		if (project != null) {
			frame.removeKeyListener(project.getKeyListener());
			project.dispose();
			contentPane.remove(project.getPanel());
		}
	}

	public void setProject(Project project) {
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

	@Deprecated
	public void dispose() {
		frame.dispose();
	}
}
