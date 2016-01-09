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
import amidst.gui.main.viewer.ViewerFacade;
import amidst.gui.main.viewer.ViewerFacadeBuilder;
import amidst.mojangapi.MojangApi;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.WorldSeed;
import amidst.mojangapi.world.WorldType;
import amidst.mojangapi.world.player.MovablePlayerList;
import amidst.mojangapi.world.player.WorldPlayerType;
import amidst.settings.biomecolorprofile.BiomeColorProfileDirectory;
import amidst.threading.ThreadMaster;

@NotThreadSafe
public class MainWindow {
	private final Application application;
	private final AmidstMetaData metadata;
	private final Settings settings;
	private final MojangApi mojangApi;
	private final BiomeColorProfileDirectory biomeColorProfileDirectory;
	private final ViewerFacadeBuilder viewerFacadeBuilder;
	private final ThreadMaster threadMaster;

	private final JFrame frame;
	private final Container contentPane;
	private final Actions actions;
	private final AmidstMenu menuBar;

	private final AtomicReference<ViewerFacade> viewerFacade = new AtomicReference<ViewerFacade>();

	@CalledOnlyBy(AmidstThread.EDT)
	public MainWindow(Application application, AmidstMetaData metadata,
			Settings settings, MojangApi mojangApi,
			BiomeColorProfileDirectory biomeColorProfileDirectory,
			ViewerFacadeBuilder viewerFacadeBuilder, ThreadMaster threadMaster) {
		this.application = application;
		this.metadata = metadata;
		this.settings = settings;
		this.mojangApi = mojangApi;
		this.biomeColorProfileDirectory = biomeColorProfileDirectory;
		this.viewerFacadeBuilder = viewerFacadeBuilder;
		this.threadMaster = threadMaster;
		this.frame = createFrame();
		this.contentPane = createContentPane();
		this.actions = createActions();
		this.menuBar = createMenuBar();
		initKeyListener();
		initCloseListener();
		showFrame();
		clearViewerFacade();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private JFrame createFrame() {
		JFrame frame = new JFrame();
		frame.setTitle(createVersionString(mojangApi.getVersionId(),
				mojangApi.getRecognisedVersionName()));
		frame.setSize(1000, 800);
		frame.setIconImage(metadata.getIcon());
		return frame;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private String createVersionString(String versionId,
			String recognisedVersionName) {
		return metadata.getVersion().createLongVersionString()
				+ " - Minecraft Version " + versionId + " ("
				+ recognisedVersionName + ")";
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private Container createContentPane() {
		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new BorderLayout());
		return contentPane;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private Actions createActions() {
		return new Actions(application, mojangApi, this, viewerFacade,
				settings.biomeColorProfileSelection,
				threadMaster.getWorkerExecutor());
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private AmidstMenu createMenuBar() {
		AmidstMenu menuBar = new AmidstMenuBuilder(settings, actions,
				biomeColorProfileDirectory).construct();
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
	public void setWorld(World world) {
		clearViewerFacade();
		if (decideWorldPlayerType(world.getMovablePlayerList())) {
			setViewerFacade(viewerFacadeBuilder.create(world, actions,
					menuBar.getSelectedDimension()));
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
	private void setViewerFacade(ViewerFacade viewerFacade) {
		contentPane.add(viewerFacade.getComponent(), BorderLayout.CENTER);
		menuBar.setWorldMenuEnabled(true);
		menuBar.setSavePlayerLocationsMenuEnabled(viewerFacade
				.canSavePlayerLocations());
		menuBar.setReloadPlayerLocationsMenuEnabled(viewerFacade
				.canLoadPlayerLocations());
		frame.validate();
		viewerFacade.loadPlayers(threadMaster.getWorkerExecutor());
		threadMaster.setOnRepaintTick(viewerFacade.getOnRepainterTick());
		threadMaster.setOnFragmentLoadTick(viewerFacade
				.getOnFragmentLoaderTick());
		this.viewerFacade.set(viewerFacade);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void clearViewerFacade() {
		threadMaster.clearOnRepaintTick();
		threadMaster.clearOnFragmentLoadTick();
		ViewerFacade viewerFacade = this.viewerFacade.getAndSet(null);
		if (viewerFacade != null) {
			contentPane.remove(viewerFacade.getComponent());
			viewerFacade.dispose();
		}
		clearViewerFacadeFromGui();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void clearViewerFacadeFromGui() {
		menuBar.setWorldMenuEnabled(false);
		menuBar.setSavePlayerLocationsMenuEnabled(false);
		menuBar.setReloadPlayerLocationsMenuEnabled(false);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void dispose() {
		clearViewerFacade();
		frame.dispose();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private WorldPlayerType askForWorldPlayerType() {
		return askForOptions(
				"Loading World",
				"This world contains Multiplayer and Singleplayer data. What do you want to load?\n"
						+ "If you do not know what to do, just choose Singleplayer.",
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
		if (worldTypeSetting.equals(WorldType.PROMPT_EACH_TIME)) {
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
