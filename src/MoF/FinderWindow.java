package MoF;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import amidst.Amidst;
import amidst.gui.menu.AmidstMenu;

public class FinderWindow extends JFrame {
	public static FinderWindow instance;
	private Container pane;
	private Project project;
	private final AmidstMenu menuBar;

	public FinderWindow() {
		super("Amidst v" + Amidst.version());

		setSize(1000, 800);
		pane = getContentPane();
		pane.setLayout(new BorderLayout());
		new UpdateManager(this, true).start();
		setJMenuBar(menuBar = new AmidstMenu(this));
		setVisible(true);
		setIconImage(Amidst.icon);
		instance = this;

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
				System.exit(0);
			}
		});
	}

	public void clearProject() {
		// TODO: Release resources
		if (project != null) {
			removeKeyListener(project.getKeyListener());
			project.dispose();
			pane.remove(project.getPanel());
		}
	}

	public void setProject(Project project) {
		menuBar.mapMenu.setEnabled(true);
		this.project = project;

		addKeyListener(project.getKeyListener());
		pane.add(this.project.getPanel(), BorderLayout.CENTER);

		this.validate();
	}

	public Project getProject() {
		return project;
	}
}
