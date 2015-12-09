package amidst.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import amidst.AmidstMetaData;
import amidst.Application;
import amidst.Options;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.gui.menu.AmidstMenu;
import amidst.gui.menu.AmidstMenuBuilder;
import amidst.gui.menu.LevelFileFilter;
import amidst.gui.menu.MenuActions;
import amidst.gui.menu.PNGFileFilter;
import amidst.gui.worldsurroundings.WorldSurroundings;
import amidst.gui.worldsurroundings.WorldSurroundingsBuilder;
import amidst.mojangapi.MojangApi;
import amidst.mojangapi.world.CoordinatesInWorld;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.WorldType;
import amidst.utilities.SeedHistoryLogger;

public class MainWindow {
	private final SeedPrompt seedPrompt = new SeedPrompt();

	private final Application application;
	private final Options options;
	private final MojangApi mojangApi;
	private final WorldSurroundingsBuilder worldSurroundingsBuilder;
	private final SeedHistoryLogger seedHistoryLogger;
	private final SkinLoader skinLoader;
	private final UpdatePrompt updatePrompt;

	private final JFrame frame;
	private final Container contentPane;
	private final MenuActions actions;
	private final AmidstMenu menuBar;

	private final AtomicReference<WorldSurroundings> worldSurroundings = new AtomicReference<WorldSurroundings>();

	public MainWindow(Application application, Options options,
			MojangApi mojangApi,
			WorldSurroundingsBuilder worldSurroundingsBuilder,
			SeedHistoryLogger seedHistoryLogger, SkinLoader skinLoader,
			UpdatePrompt updatePrompt) {
		this.application = application;
		this.options = options;
		this.mojangApi = mojangApi;
		this.worldSurroundingsBuilder = worldSurroundingsBuilder;
		this.seedHistoryLogger = seedHistoryLogger;
		this.skinLoader = skinLoader;
		this.updatePrompt = updatePrompt;
		this.frame = createFrame();
		this.contentPane = createContentPane();
		this.actions = createMenuActions();
		this.menuBar = createMenuBar();
		initKeyListener();
		initCloseListener();
		showFrame();
		checkForUpdates();
		clearWorldSurroundings();
	}

	// TODO: use official minecraft version id instead of recognised one?
	private JFrame createFrame() {
		JFrame frame = new JFrame();
		frame.setTitle(createVersionString(mojangApi.getRecognisedVersionName()));
		frame.setSize(1000, 800);
		frame.setIconImage(AmidstMetaData.ICON);
		return frame;
	}

	private String createVersionString(String version) {
		return "Amidst v" + AmidstMetaData.getFullVersionString()
				+ " [Using Minecraft version: " + version + "]";
	}

	private Container createContentPane() {
		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new BorderLayout());
		return contentPane;
	}

	private MenuActions createMenuActions() {
		return new MenuActions(application, mojangApi, this, worldSurroundings,
				skinLoader, updatePrompt, options.biomeColorProfileSelection);
	}

	private AmidstMenu createMenuBar() {
		AmidstMenu menuBar = new AmidstMenuBuilder(options, actions)
				.construct();
		frame.setJMenuBar(menuBar.getMenuBar());
		return menuBar;
	}

	private void initKeyListener() {
		frame.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_PLUS) {
					actions.adjustZoom(-1);
				} else if (e.getKeyCode() == KeyEvent.VK_MINUS) {
					actions.adjustZoom(1);
				}
			}
		});
	}

	private void initCloseListener() {
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				actions.exit();
			}
		});
	}

	private void showFrame() {
		frame.setVisible(true);
	}

	private void checkForUpdates() {
		updatePrompt.checkSilently(this);
	}

	public void setWorld(World world) {
		setWorldSurroundings(worldSurroundingsBuilder.create(world));
	}

	/**
	 * This ensures that the instance variable worldSurroundings is assigned
	 * AFTER frame.validate() is called. This is important, because the
	 * repainter thread will draw the new worldSurroundings as soon as it is
	 * assigned to the instance variable.
	 */
	public void setWorldSurroundings(WorldSurroundings worldSurroundings) {
		clearWorldSurroundings();
		seedHistoryLogger.log(worldSurroundings.getSeed());
		contentPane.add(worldSurroundings.getComponent(), BorderLayout.CENTER);
		menuBar.setWorldMenuEnabled(true);
		menuBar.setPlayerLocationMenusEnabled(worldSurroundings
				.canSavePlayerLocations());
		frame.validate();
		worldSurroundings.loadPlayerSkins(skinLoader);
		this.worldSurroundings.set(worldSurroundings);
	}

	private void clearWorldSurroundings() {
		WorldSurroundings worldSurroundings = this.worldSurroundings
				.getAndSet(null);
		if (worldSurroundings != null) {
			contentPane.remove(worldSurroundings.getComponent());
			worldSurroundings.dispose();
		}
		clearWorldSurroundingsFromGui();
	}

	private void clearWorldSurroundingsFromGui() {
		menuBar.setWorldMenuEnabled(false);
		menuBar.setPlayerLocationMenusEnabled(false);
	}

	public void dispose() {
		clearWorldSurroundings();
		frame.dispose();
	}

	public String askForSeed() {
		return seedPrompt.askForSeed(frame);
	}

	public File askForMinecraftWorldFile() {
		return getSelectedFileOrNull(createMinecraftWorldFileChooser());
	}

	private JFileChooser createMinecraftWorldFileChooser() {
		JFileChooser result = new JFileChooser();
		result.setFileFilter(new LevelFileFilter());
		result.setAcceptAllFileFilterUsed(false);
		result.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		result.setCurrentDirectory(mojangApi.getSaves());
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

	// TODO: revisit error handling and feedback on gui
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

	public WorldType askForWorldType() {
		String worldTypePreference = options.worldType.get();
		if (worldTypePreference.equals("Prompt each time")) {
			return askForOptions("New Project", "Enter world type\n",
					WorldType.getSelectable());
		} else {
			return WorldType.from(worldTypePreference);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T askForOptions(String title, String message, List<T> choices) {
		Object[] choicesArray = choices.toArray();
		return (T) JOptionPane.showInputDialog(frame, message, title,
				JOptionPane.PLAIN_MESSAGE, null, choicesArray, choicesArray[0]);
	}

	public CoordinatesInWorld askForCoordinates() {
		String coordinates = askForString("Go To",
				"Enter coordinates: (Ex. 123,456)");
		if (coordinates != null) {
			return CoordinatesInWorld.fromString(coordinates);
		} else {
			return null;
		}
	}

	private String askForString(String title, String message) {
		return JOptionPane.showInputDialog(frame, message, title,
				JOptionPane.QUESTION_MESSAGE);
	}

	@CalledOnlyBy(AmidstThread.REPAINTER)
	public void tickRepainter() {
		WorldSurroundings worldSurroundings = this.worldSurroundings.get();
		if (worldSurroundings != null) {
			worldSurroundings.tickRepainter();
		}
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	public void tickFragmentLoader() {
		WorldSurroundings worldSurroundings = this.worldSurroundings.get();
		if (worldSurroundings != null) {
			worldSurroundings.tickFragmentLoader();
		}
	}
}
