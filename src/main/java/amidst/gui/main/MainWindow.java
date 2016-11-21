package amidst.gui.main;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import amidst.AmidstMetaData;
import amidst.AmidstSettings;
import amidst.Application;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.gui.main.menu.AmidstMenu;
import amidst.gui.main.menu.AmidstMenuBuilder;
import amidst.gui.main.viewer.ViewerFacade;
import amidst.gui.main.viewer.ViewerFacadeBuilder;
import amidst.gui.seedsearcher.SeedSearcher;
import amidst.gui.seedsearcher.SeedSearcherWindow;
import amidst.logging.AmidstLogger;
import amidst.logging.AmidstMessageBox;
import amidst.mojangapi.MojangApi;
import amidst.mojangapi.file.MojangApiParsingException;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.WorldSeed;
import amidst.mojangapi.world.WorldType;
import amidst.mojangapi.world.export.WorldExporterConfiguration;
import amidst.mojangapi.world.player.MovablePlayerList;
import amidst.mojangapi.world.player.WorldPlayerType;
import amidst.settings.biomeprofile.BiomeProfileDirectory;
import amidst.threading.ThreadMaster;

@NotThreadSafe
public class MainWindow {
	private final Application application;
	private final AmidstMetaData metadata;
	private final AmidstSettings settings;
	private final MojangApi mojangApi;
	private final BiomeProfileDirectory biomeProfileDirectory;
	private final ViewerFacadeBuilder viewerFacadeBuilder;
	private final ThreadMaster threadMaster;

	private final JFrame frame;
	private final Container contentPane;
	private final Actions actions;
	private final AmidstMenu menuBar;
	private final SeedSearcherWindow seedSearcherWindow;

	private final AtomicReference<ViewerFacade> viewerFacade = new AtomicReference<>();

	@CalledOnlyBy(AmidstThread.EDT)
	public MainWindow(
			Application application,
			AmidstMetaData metadata,
			AmidstSettings settings,
			MojangApi mojangApi,
			BiomeProfileDirectory biomeProfileDirectory,
			ViewerFacadeBuilder viewerFacadeBuilder,
			ThreadMaster threadMaster) {
		this.application = application;
		this.metadata = metadata;
		this.settings = settings;
		this.mojangApi = mojangApi;
		this.biomeProfileDirectory = biomeProfileDirectory;
		this.viewerFacadeBuilder = viewerFacadeBuilder;
		this.threadMaster = threadMaster;
		this.frame = createFrame();
		this.contentPane = createContentPane();
		this.actions = createActions();
		this.menuBar = createMenuBar();
		this.seedSearcherWindow = createSeedSearcherWindow();
		initCloseListener();
		showFrame();
		clearViewerFacade();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private JFrame createFrame() {
		JFrame frame = new JFrame();
		frame.setTitle(
				createVersionString(
						mojangApi.getVersionId(),
						mojangApi.getRecognisedVersionName(),
						mojangApi.getProfileName()));
		frame.setSize(1000, 800);
		frame.setIconImages(metadata.getIcons());
		return frame;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private String createVersionString(String versionId, String recognisedVersionName, String profileName) {
		return metadata.getVersion().createLongVersionString() + " - Selected Profile: " + profileName
				+ " - Minecraft Version " + versionId + " (recognised: " + recognisedVersionName + ")";
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private Container createContentPane() {
		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new BorderLayout());
		return contentPane;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private Actions createActions() {
		return new Actions(application, this, viewerFacade, settings.biomeProfileSelection);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private AmidstMenu createMenuBar() {
		AmidstMenu menuBar = new AmidstMenuBuilder(settings, actions, biomeProfileDirectory).construct();
		frame.setJMenuBar(menuBar.getMenuBar());
		return menuBar;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private SeedSearcherWindow createSeedSearcherWindow() {
		return new SeedSearcherWindow(
				metadata,
				this,
				new SeedSearcher(this, mojangApi, threadMaster.getWorkerExecutor()));
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
	public void displayWorld(WorldSeed worldSeed, WorldType worldType) {
		try {
			setWorld(mojangApi.createWorldFromSeed(worldSeed, worldType));
		} catch (IllegalStateException | MinecraftInterfaceException e) {
			AmidstLogger.warn(e);
			displayError(e);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void displayWorld(File file) {
		try {
			setWorld(mojangApi.createWorldFromSaveGame(file));
		} catch (IllegalStateException | MinecraftInterfaceException | IOException | MojangApiParsingException e) {
			AmidstLogger.warn(e);
			displayError(e);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void setWorld(World world) {
		clearViewerFacade();
		if (decideWorldPlayerType(world.getMovablePlayerList())) {
			setViewerFacade(viewerFacadeBuilder.create(world, actions));
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
		menuBar.set(viewerFacade);
		frame.validate();
		viewerFacade.loadPlayers();
		threadMaster.setOnRepaintTick(viewerFacade.getOnRepainterTick());
		threadMaster.setOnFragmentLoadTick(viewerFacade.getOnFragmentLoaderTick());
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
		menuBar.clear();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void dispose() {
		clearViewerFacade();
		seedSearcherWindow.dispose();
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
	public File askForSaveGame() {
		return showOpenDialogAndGetSelectedFileOrNull(createSaveGameFileChooser());
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private JFileChooser createSaveGameFileChooser() {
		JFileChooser result = new JFileChooser(mojangApi.getSaves());
		result.setFileFilter(new LevelFileFilter());
		result.setAcceptAllFileFilterUsed(false);
		result.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		result.setFileHidingEnabled(false);
		return result;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private File showOpenDialogAndGetSelectedFileOrNull(JFileChooser fileChooser) {
		if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFile();
		} else {
			return null;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public File askForCaptureImageSaveFile(String suggestedFilename) {
		return showSaveDialogAndGetSelectedFileOrNull(createCaptureImageSaveFileChooser(suggestedFilename));
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private JFileChooser createCaptureImageSaveFileChooser(String suggestedFilename) {
		JFileChooser result = new JFileChooser();
		result.setFileFilter(new PNGFileFilter());
		result.setAcceptAllFileFilterUsed(false);
		result.setSelectedFile(new File(suggestedFilename));
		return result;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private File showSaveDialogAndGetSelectedFileOrNull(JFileChooser fileChooser) {
		if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFile();
		} else {
			return null;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void displayInfo(String title, String message) {
		AmidstMessageBox.displayInfo(frame, title, message);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void displayError(String message) {
		AmidstMessageBox.displayError(frame, "Error", message);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void displayError(Exception e) {
		AmidstMessageBox.displayError(frame, "Error", e);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public boolean askToConfirmSaveGameManipulation() {
		return askToConfirmYesNo(
				"Save Game Manipulation",
				"WARNING: You are about to change the contents of the save game directory. There is a chance that it gets corrupted.\n"
						+ "We try to minimize the risk by creating a backup of the changed file, before it is changed.\n"
						+ "If the backup fails, we will not write the changes.\n"
						+ "You can find the backup files in the directory 'amidst/backup', which is placed in the save game directory.\n"
						+ "Especially, make sure to not have the save game loaded in Minecraft during this process.\n\n"
						+ "Do you want to proceed?");
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public boolean askToConfirmYesNo(String title, String message) {
		return AmidstMessageBox.askToConfirmYesNo(frame, title, message);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public WorldType askForWorldType() {
		String worldTypeSetting = settings.worldType.get();
		if (worldTypeSetting.equals(WorldType.PROMPT_EACH_TIME)) {
			return askForOptions("World Type", "Enter world type\n", WorldType.getSelectable());
		} else {
			return WorldType.from(worldTypeSetting);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@SuppressWarnings("unchecked")
	public <T> T askForOptions(String title, String message, List<T> choices) {
		Object[] choicesArray = choices.toArray();
		return (T) JOptionPane
				.showInputDialog(frame, message, title, JOptionPane.PLAIN_MESSAGE, null, choicesArray, choicesArray[0]);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public String askForCoordinates() {
		return askForString("Go To", "Enter coordinates: (Ex. 123,456)");
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public String askForPlayerHeight(long currentHeight) {
		Object input = JOptionPane.showInputDialog(
				frame,
				"Enter new player height:",
				"Move Player",
				JOptionPane.QUESTION_MESSAGE,
				null,
				null,
				currentHeight);
		if (input != null) {
			return input.toString();
		} else {
			return null;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private String askForString(String title, String message) {
		return JOptionPane.showInputDialog(frame, message, title, JOptionPane.QUESTION_MESSAGE);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void displaySeedSearcherWindow() {
		seedSearcherWindow.show();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public WorldExporterConfiguration askForExportConfiguration() {
		// TODO: implement me!
		// TODO: display gui to create configuration
		return new WorldExporterConfiguration();
	}
}
