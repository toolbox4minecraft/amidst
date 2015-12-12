package amidst.gui.main;

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
import amidst.Settings;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.gui.main.menu.AmidstMenu;
import amidst.gui.main.menu.AmidstMenuBuilder;
import amidst.gui.main.worldsurroundings.WorldSurroundings;
import amidst.gui.main.worldsurroundings.WorldSurroundingsBuilder;
import amidst.mojangapi.MojangApi;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.WorldSeed;
import amidst.mojangapi.world.WorldType;
import amidst.mojangapi.world.player.MovablePlayerList;
import amidst.mojangapi.world.player.WorldPlayerType;
import amidst.threading.ThreadMaster;
import amidst.utilities.SeedHistoryLogger;

@NotThreadSafe
public class MainWindow {
	private final Application application;
	private final Settings settings;
	private final MojangApi mojangApi;
	private final WorldSurroundingsBuilder worldSurroundingsBuilder;
	private final SeedHistoryLogger seedHistoryLogger;
	private final UpdatePrompt updatePrompt;
	private final ThreadMaster threadMaster;

	private final JFrame frame;
	private final Container contentPane;
	private final Actions actions;
	private final AmidstMenu menuBar;

	private final AtomicReference<WorldSurroundings> worldSurroundings = new AtomicReference<WorldSurroundings>();

	@CalledOnlyBy(AmidstThread.EDT)
	public MainWindow(Application application, Settings settings,
			MojangApi mojangApi,
			WorldSurroundingsBuilder worldSurroundingsBuilder,
			SeedHistoryLogger seedHistoryLogger, UpdatePrompt updatePrompt,
			ThreadMaster threadMaster) {
		this.application = application;
		this.settings = settings;
		this.mojangApi = mojangApi;
		this.worldSurroundingsBuilder = worldSurroundingsBuilder;
		this.seedHistoryLogger = seedHistoryLogger;
		this.updatePrompt = updatePrompt;
		this.threadMaster = threadMaster;
		this.frame = createFrame();
		this.contentPane = createContentPane();
		this.actions = createActions();
		this.menuBar = createMenuBar();
		initKeyListener();
		initCloseListener();
		showFrame();
		checkForUpdates();
		clearWorldSurroundings();
	}

	// TODO: use official minecraft version id instead of recognised one?
	@CalledOnlyBy(AmidstThread.EDT)
	private JFrame createFrame() {
		JFrame frame = new JFrame();
		frame.setTitle(createVersionString(mojangApi.getRecognisedVersionName()));
		frame.setSize(1000, 800);
		frame.setIconImage(AmidstMetaData.ICON);
		return frame;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private String createVersionString(String version) {
		return "Amidst v" + AmidstMetaData.getFullVersionString()
				+ " [Using Minecraft version: " + version + "]";
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private Container createContentPane() {
		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new BorderLayout());
		return contentPane;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private Actions createActions() {
		return new Actions(application, mojangApi, this, worldSurroundings,
				updatePrompt, settings.biomeColorProfileSelection,
				threadMaster.getWorkerExecutor());
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private AmidstMenu createMenuBar() {
		AmidstMenu menuBar = new AmidstMenuBuilder(settings, actions)
				.construct();
		frame.setJMenuBar(menuBar.getMenuBar());
		return menuBar;
	}

	@CalledOnlyBy(AmidstThread.EDT)
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

	@CalledOnlyBy(AmidstThread.EDT)
	private void initCloseListener() {
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				actions.exit();
			}
		});
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void showFrame() {
		frame.setVisible(true);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void checkForUpdates() {
		updatePrompt.checkSilently(this);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void setWorld(World world) {
		clearWorldSurroundings();
		if (decideWorldPlayerType(world.getMovablePlayerList())) {
			setWorldSurroundings(worldSurroundingsBuilder
					.create(world, actions));
		} else {
			frame.revalidate();
			frame.repaint();
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private boolean decideWorldPlayerType(MovablePlayerList movablePlayerList) {
		if (movablePlayerList.getWorldPlayerType().equals(WorldPlayerType.BOTH)) {
			WorldPlayerType worldPlayerType = askForWorldPlayerType();
			if (worldPlayerType != null) {
				movablePlayerList.setWorldPlayerType(worldPlayerType);
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void setWorldSurroundings(WorldSurroundings worldSurroundings) {
		seedHistoryLogger.log(worldSurroundings.getWorldSeed());
		contentPane.add(worldSurroundings.getComponent(), BorderLayout.CENTER);
		menuBar.setWorldMenuEnabled(true);
		menuBar.setSavePlayerLocationsMenuEnabled(worldSurroundings
				.canSavePlayerLocations());
		menuBar.setReloadPlayerLocationsMenuEnabled(worldSurroundings
				.canLoadPlayerLocations());
		frame.validate();
		worldSurroundings.loadPlayers(threadMaster.getWorkerExecutor());
		threadMaster.setOnRepaintTick(worldSurroundings.getOnRepainterTick());
		threadMaster.setOnFragmentLoadTick(worldSurroundings
				.getOnFragmentLoaderTick());
		this.worldSurroundings.set(worldSurroundings);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void clearWorldSurroundings() {
		threadMaster.clearOnRepaintTick();
		threadMaster.clearOnFragmentLoadTick();
		WorldSurroundings worldSurroundings = this.worldSurroundings
				.getAndSet(null);
		if (worldSurroundings != null) {
			contentPane.remove(worldSurroundings.getComponent());
			worldSurroundings.dispose();
		}
		clearWorldSurroundingsFromGui();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void clearWorldSurroundingsFromGui() {
		menuBar.setWorldMenuEnabled(false);
		menuBar.setSavePlayerLocationsMenuEnabled(false);
		menuBar.setReloadPlayerLocationsMenuEnabled(false);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void dispose() {
		clearWorldSurroundings();
		frame.dispose();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private WorldPlayerType askForWorldPlayerType() {
		return askForOptions("Loading World",
				"This world contains Multiplayer and Singleplayer data.\n"
						+ "What do you want to load?",
				WorldPlayerType.getSelectable());
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public WorldSeed askForSeed() {
		return new SeedPrompt(frame).askForSeed();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public File askForMinecraftWorldFile() {
		return getSelectedFileOrNull(createMinecraftWorldFileChooser());
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private JFileChooser createMinecraftWorldFileChooser() {
		JFileChooser result = new JFileChooser();
		result.setFileFilter(new LevelFileFilter());
		result.setAcceptAllFileFilterUsed(false);
		result.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		result.setCurrentDirectory(mojangApi.getSaves());
		result.setFileHidingEnabled(false);
		return result;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public File askForCaptureImageSaveFile() {
		return getSelectedFileOrNull(createCaptureImageSaveFileChooser());
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private JFileChooser createCaptureImageSaveFileChooser() {
		JFileChooser result = new JFileChooser();
		result.setFileFilter(new PNGFileFilter());
		result.setAcceptAllFileFilterUsed(false);
		return result;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private File getSelectedFileOrNull(JFileChooser fileChooser) {
		if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFile();
		} else {
			return null;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void displayMessage(String title, String message) {
		JOptionPane.showMessageDialog(frame, message, title,
				JOptionPane.INFORMATION_MESSAGE);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void displayError(String message) {
		JOptionPane.showMessageDialog(frame, message, "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	// TODO: revisit error handling and feedback on gui
	@CalledOnlyBy(AmidstThread.EDT)
	public void displayException(Exception exception) {
		JOptionPane.showMessageDialog(frame, getStackTraceAsString(exception),
				"Error", JOptionPane.ERROR_MESSAGE);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private String getStackTraceAsString(Exception exception) {
		StringWriter writer = new StringWriter();
		exception.printStackTrace(new PrintWriter(writer));
		return writer.toString();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public boolean askToConfirm(String title, String message) {
		return JOptionPane.showConfirmDialog(frame, message, title,
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public WorldType askForWorldType() {
		String worldTypeSetting = settings.worldType.get();
		if (worldTypeSetting.equals("Prompt each time")) {
			return askForOptions("World Type", "Enter world type\n",
					WorldType.getSelectable());
		} else {
			return WorldType.from(worldTypeSetting);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@SuppressWarnings("unchecked")
	public <T> T askForOptions(String title, String message, List<T> choices) {
		Object[] choicesArray = choices.toArray();
		return (T) JOptionPane.showInputDialog(frame, message, title,
				JOptionPane.PLAIN_MESSAGE, null, choicesArray, choicesArray[0]);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public String askForCoordinates() {
		return askForString("Go To", "Enter coordinates: (Ex. 123,456)");
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public String askForPlayerHeight(long currentHeight) {
		Object input = JOptionPane.showInputDialog(frame,
				"Enter new player height:", "Move Player",
				JOptionPane.QUESTION_MESSAGE, null, null, currentHeight);
		if (input != null) {
			return input.toString();
		} else {
			return null;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private String askForString(String title, String message) {
		return JOptionPane.showInputDialog(frame, message, title,
				JOptionPane.QUESTION_MESSAGE);
	}
}
