package amidst.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import amidst.AmidstMetaData;
import amidst.Application;
import amidst.Options;
import amidst.Util;
import amidst.gui.menu.AmidstMenu;
import amidst.gui.menu.LevelFileFilter;
import amidst.gui.menu.PNGFileFilter;
import amidst.map.MapViewer;
import amidst.minecraft.MinecraftUtil;
import amidst.minecraft.world.WorldType;

public class MapWindow {
	private static MapWindow instance;

	public static MapWindow getInstance() {
		return instance;
	}

	private Application application;
	private MapViewer mapViewer;
	private JPanel panel;

	private JFrame frame = new JFrame();
	private AmidstMenu menuBar;
	private Container contentPane;

	private SeedPrompt seedPrompt = new SeedPrompt(frame);

	public MapWindow(Application application) {
		this.application = application;
		frame.setTitle("Amidst v" + getVersionString());
		frame.setSize(1000, 800);
		frame.setIconImage(AmidstMetaData.ICON);
		initContentPane();
		initUpdateManager();
		initMenuBar();
		initCloseListener();
		instance = this;
		frame.setVisible(true);
	}

	private String getVersionString() {
		if (MinecraftUtil.hasInterface()) {
			return AmidstMetaData.getFullVersionString()
					+ " [Using Minecraft version: "
					+ MinecraftUtil.getVersion() + "]";
		} else {
			return AmidstMetaData.getFullVersionString();
		}
	}

	private void initContentPane() {
		contentPane = frame.getContentPane();
		contentPane.setLayout(new BorderLayout());
	}

	private void initUpdateManager() {
		application.checkForUpdatesSilently();
	}

	private void initMenuBar() {
		menuBar = new AmidstMenu(application, this);
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

	@Deprecated
	public JFrame getFrame() {
		return frame;
	}

	public void dispose() {
		setMapViewer(null);
		frame.dispose();
	}

	private void setMapViewer(MapViewer mapViewer) {
		clearMapViewer();
		this.mapViewer = mapViewer;
		if (mapViewer != null) {
			createPanel(mapViewer);
			menuBar.setMapMenuEnabled(true);
			frame.addKeyListener(mapViewer.getKeyListener());
			contentPane.add(panel, BorderLayout.CENTER);
			frame.validate();
		}
	}

	private void createPanel(MapViewer mapViewer) {
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(mapViewer.getComponent(), BorderLayout.CENTER);
		panel.setBackground(Color.BLUE);
	}

	private void clearMapViewer() {
		if (mapViewer != null) {
			frame.removeKeyListener(mapViewer.getKeyListener());
			mapViewer.dispose();
			contentPane.remove(panel);
		}
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

	public int askToConfirm(String title, String message) {
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

	public void moveMapToCoordinates(long x, long y) {
		mapViewer.centerAt(x, y);
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

	public void worldChanged() {
		setMapViewer(new MapViewer(application));
	}

	public void capture(File file) {
		BufferedImage image = mapViewer.createCaptureImage();
		saveToFile(image, file);
		image.flush();
	}

	private void saveToFile(BufferedImage image, File file) {
		try {
			ImageIO.write(image, "png", addPNGFileExtensionIfNecessary(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private File addPNGFileExtensionIfNecessary(File file) {
		String filename = file.toString();
		if (!filename.toLowerCase().endsWith(".png")) {
			filename += ".png";
		}
		return new File(filename);
	}

	public long[] askForCoordinates() {
		String coordinates = askForString("Go To",
				"Enter coordinates: (Ex. 123,456)");
		if (coordinates != null) {
			return parseCoordinates(coordinates);
		} else {
			return null;
		}
	}

	private String askForString(String title, String message) {
		return JOptionPane.showInputDialog(frame, message, title,
				JOptionPane.QUESTION_MESSAGE);
	}

	private long[] parseCoordinates(String coordinates) {
		String[] parsedCoordinates = coordinates.replaceAll(" ", "").split(",");
		if (parsedCoordinates.length != 2) {
			return null;
		}
		try {
			long[] result = new long[2];
			result[0] = Long.parseLong(parsedCoordinates[0]);
			result[1] = Long.parseLong(parsedCoordinates[1]);
			return result;
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
