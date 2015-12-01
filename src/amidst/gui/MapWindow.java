package amidst.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import amidst.AmidstMetaData;
import amidst.Application;
import amidst.Options;
import amidst.gui.menu.AmidstMenu;
import amidst.gui.menu.LevelFileFilter;
import amidst.gui.menu.PNGFileFilter;
import amidst.map.BiomeSelection;
import amidst.map.Map;
import amidst.map.MapFactory;
import amidst.map.MapMovement;
import amidst.map.MapViewer;
import amidst.map.MapZoom;
import amidst.minecraft.LocalMinecraftInstallation;
import amidst.minecraft.MinecraftUtil;
import amidst.minecraft.world.CoordinatesInWorld;
import amidst.minecraft.world.WorldType;

public class MapWindow {
	private final SeedPrompt seedPrompt = new SeedPrompt();
	private final MapZoom mapZoom = new MapZoom();
	private final MapMovement mapMovement = new MapMovement();
	private final BiomeSelection biomeSelection = new BiomeSelection();

	private final Application application;
	private final MapFactory mapFactory;

	private final JFrame frame;
	private final Container contentPane;
	private final AmidstMenu menuBar;

	private MapViewer mapViewer;
	private Map map;

	public MapWindow(Application application, MapFactory mapFactory,
			Options preferences) {
		this.application = application;
		this.mapFactory = mapFactory;
		this.frame = createFrame();
		this.contentPane = createContentPane();
		this.menuBar = createMenuBar(preferences);
		initKeyListener();
		initCloseListener();
		showFrame();
		checkForUpdates();
	}

	private JFrame createFrame() {
		JFrame frame = new JFrame();
		frame.setTitle(getVersionString());
		frame.setSize(1000, 800);
		frame.setIconImage(AmidstMetaData.ICON);
		return frame;
	}

	private String getVersionString() {
		if (MinecraftUtil.hasInterface()) {
			return "Amidst v" + AmidstMetaData.getFullVersionString()
					+ " [Using Minecraft version: "
					+ MinecraftUtil.getVersion() + "]";
		} else {
			return "Amidst v" + AmidstMetaData.getFullVersionString();
		}
	}

	private Container createContentPane() {
		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new BorderLayout());
		return contentPane;
	}

	private AmidstMenu createMenuBar(Options preferences) {
		AmidstMenu menuBar = new AmidstMenu(application, preferences, this);
		frame.setJMenuBar(menuBar.getMenuBar());
		return menuBar;
	}

	private void initKeyListener() {
		frame.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (mapViewer != null) {
					Point mouse = mapViewer.getMousePositionOrCenter();
					if (e.getKeyCode() == KeyEvent.VK_EQUALS) {
						mapZoom.adjustZoom(mouse, -1);
					} else if (e.getKeyCode() == KeyEvent.VK_MINUS) {
						mapZoom.adjustZoom(mouse, 1);
					}
				}
			}
		});
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

	private void showFrame() {
		frame.setVisible(true);
	}

	private void checkForUpdates() {
		application.checkForUpdatesSilently();
	}

	public void dispose() {
		clearWorld();
		frame.dispose();
	}

	public void clearWorld() {
		if (mapViewer != null) {
			menuBar.disableMapMenu();
			contentPane.remove(mapViewer.getPanel());
			map.safeDispose();
			mapMovement.reset();
			mapZoom.skipFading();
			mapViewer = null;
			map = null;
		}
	}

	public void initWorld() {
		map = mapFactory
				.create(application.getWorld(), mapZoom, biomeSelection);
		mapViewer = createMapViewer();
		menuBar.enableMapMenu();
	}

	/**
	 * This ensures that the instance variable mapViewer is assigned AFTER
	 * frame.validate() is called. This is important, because the executor will
	 * draw the new mapViewer as soon as it is assigned to the instance
	 * variable.
	 */
	private MapViewer createMapViewer() {
		MapViewer result = new MapViewer(mapMovement, mapZoom,
				application.getWorld(), map);
		contentPane.add(result.getPanel(), BorderLayout.CENTER);
		frame.validate();
		return result;
	}

	public String askForSeed() {
		return seedPrompt.askForSeed(frame);
	}

	@SuppressWarnings("unchecked")
	public <T> T askForOptions(String title, String message, List<T> choices) {
		Object[] choicesArray = choices.toArray();
		return (T) JOptionPane.showInputDialog(frame, message, title,
				JOptionPane.PLAIN_MESSAGE, null, choicesArray, choicesArray[0]);
	}

	public File askForMinecraftMapFile() {
		return getSelectedFileOrNull(createMinecraftMapFileChooser());
	}

	private JFileChooser createMinecraftMapFileChooser() {
		JFileChooser result = new JFileChooser();
		result.setFileFilter(new LevelFileFilter());
		result.setAcceptAllFileFilterUsed(false);
		result.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		result.setCurrentDirectory(LocalMinecraftInstallation
				.getSavesDirectory());
		result.setFileHidingEnabled(false);
		return result;
	}

	public File askForScreenshotSaveFile() {
		return getSelectedFileOrNull(createScreenshotSaveFileChooser());
	}

	private JFileChooser createScreenshotSaveFileChooser() {
		JFileChooser result = new JFileChooser();
		result.setFileFilter(new PNGFileFilter());
		result.setAcceptAllFileFilterUsed(false);
		return result;
	}

	private File getSelectedFileOrNull(JFileChooser fileChooser) {
		if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFile();
		} else {
			return null;
		}
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

	public void moveMapToCoordinates(CoordinatesInWorld coordinates) {
		map.safeCenterOn(coordinates);
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

	public void capture(File file) {
		BufferedImage image = mapViewer.createCaptureImage();
		saveToFile(image, file);
		image.flush();
	}

	private void saveToFile(BufferedImage image, File file) {
		try {
			ImageIO.write(image, "png", appendPNGFileExtensionIfNecessary(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private File appendPNGFileExtensionIfNecessary(File file) {
		String filename = file.toString();
		if (!filename.toLowerCase().endsWith(".png")) {
			filename += ".png";
		}
		return new File(filename);
	}

	public CoordinatesInWorld askForCoordinates() {
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

	private CoordinatesInWorld parseCoordinates(String coordinates) {
		String[] parsedCoordinates = coordinates.replaceAll(" ", "").split(",");
		if (parsedCoordinates.length != 2) {
			return null;
		}
		try {
			return CoordinatesInWorld.from(
					Long.parseLong(parsedCoordinates[0]),
					Long.parseLong(parsedCoordinates[1]));
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public void reloadBiomeLayer() {
		if (map != null) {
			map.reloadBiomeLayer();
		}
	}

	public void tickRepainter() {
		if (mapViewer != null) {
			mapViewer.repaint();
		}
	}

	public void reloadPlayerLayer() {
		if (map != null) {
			map.reloadPlayerLayer();
		}
	}

	public void tickFragmentLoader() {
		if (map != null) {
			map.tickFragmentLoader();
		}
	}
}
