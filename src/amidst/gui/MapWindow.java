package amidst.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import MoF.Project;
import amidst.Amidst;
import amidst.Application;
import amidst.Options;
import amidst.Util;
import amidst.gui.menu.AmidstMenu;
import amidst.gui.menu.LevelFileFilter;
import amidst.gui.menu.PNGFileFilter;
import amidst.minecraft.world.WorldType;

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
		application.checkForUpdatesSilently();
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

	@SuppressWarnings("unchecked")
	public <T> T askForOptions(String title, String message, T[] choices) {
		return (T) JOptionPane.showInputDialog(frame, message, title,
				JOptionPane.PLAIN_MESSAGE, null, choices, choices[0]);
	}

	public File askForMinecraftMapFile() {
		return getSelectedFileOrNull(createMinecraftMapFileChooser());
	}

	private JFileChooser createMinecraftMapFileChooser() {
		JFileChooser result = new JFileChooser();
		result.setFileFilter(new LevelFileFilter());
		result.setAcceptAllFileFilterUsed(false);
		result.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		result.setCurrentDirectory(Util.getSavesDirectory());
		result.setFileHidingEnabled(false);
		return result;
	}

	public void displayMessage(String message) {
		JOptionPane.showMessageDialog(frame, message);
	}

	public void displayException(Exception exception) {
		displayMessage(getStackTraceAsString(exception));
	}

	private String getStackTraceAsString(Exception exception) {
		StringWriter writer = new StringWriter();
		exception.printStackTrace(new PrintWriter(writer));
		return writer.toString();
	}

	public int askToConfirm(String message, String title) {
		return JOptionPane.showConfirmDialog(frame, message, title,
				JOptionPane.YES_NO_OPTION);
	}

	public File askForScreenshotSaveFile() {
		return getSelectedFileOrNull(createScreenshotSaveFileChooser());
	}

	private File getSelectedFileOrNull(JFileChooser fileChooser) {
		if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFile();
		} else {
			return null;
		}
	}

	private JFileChooser createScreenshotSaveFileChooser() {
		JFileChooser result = new JFileChooser();
		result.setFileFilter(new PNGFileFilter());
		result.setAcceptAllFileFilterUsed(false);
		return result;
	}

	public void moveMapTo(long x, long y) {
		application.getProject().moveMapTo(x, y);
	}

	public WorldType askForWorldType() {
		String worldTypePreference = Options.instance.worldType.get();
		if (worldTypePreference.equals("Prompt each time")) {
			return askForOptions("New Project", "Enter world type\n",
					WorldType.getSelectable());
		} else {
			return WorldType.from(worldTypePreference);
		}
	}
}
