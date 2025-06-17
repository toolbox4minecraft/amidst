package amidst.gui.main;

import amidst.AmidstSettings;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.FragmentManager;
import amidst.fragment.layer.LayerBuilder;
import amidst.gui.export.BiomeExporterDialog;
import amidst.gui.main.menu.AmidstMenu;
import amidst.gui.main.viewer.BiomeSelection;
import amidst.gui.main.viewer.PerViewerFacadeInjector;
import amidst.gui.main.viewer.ViewerFacade;
import amidst.gui.main.viewer.Zoom;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.RunningLauncherProfile;
import amidst.mojangapi.file.MinecraftInstallation;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.WorldOptions;
import amidst.mojangapi.world.player.MovablePlayerList;
import amidst.mojangapi.world.player.WorldPlayerType;
import amidst.parsing.FormatException;
import amidst.threading.ThreadMaster;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Container;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@NotThreadSafe
public class WorldSwitcher {
	private final MinecraftInstallation minecraftInstallation;
	private final RunningLauncherProfile runningLauncherProfile;
	private final BiomeExporterDialog biomeExporterDialog;
	private final AmidstSettings settings;
	private final Zoom zoom;
	private final LayerBuilder layerBuilder;
	private final FragmentManager fragmentManager;
	private final BiomeSelection biomeSelection;
	private final Supplier<Actions> actionsSupplier;
	private final ThreadMaster threadMaster;
	private final JFrame frame;
	private final Container contentPane;
	private final AtomicReference<ViewerFacade> viewerFacadeReference;
	private final MainWindowDialogs dialogs;
	private final Supplier<AmidstMenu> menuBarSupplier;

	@CalledOnlyBy(AmidstThread.EDT)
	public WorldSwitcher(
			MinecraftInstallation minecraftInstallation,
			RunningLauncherProfile runningLauncherProfile,
			AmidstSettings settings,
			Zoom zoom,
			LayerBuilder layerBuilder,
			FragmentManager fragmentManager,
			BiomeSelection biomeSelection,
			Supplier<Actions> actionsSupplier,
			BiomeExporterDialog biomeExporterDialog,
			ThreadMaster threadMaster,
			JFrame frame,
			Container contentPane,
			AtomicReference<ViewerFacade> viewerFacadeReference,
			MainWindowDialogs dialogs,
			Supplier<AmidstMenu> menuBarSupplier) {
		this.minecraftInstallation = minecraftInstallation;
		this.runningLauncherProfile = runningLauncherProfile;
		this.settings = settings;
		this.biomeExporterDialog = biomeExporterDialog;
		this.zoom = zoom;
		this.layerBuilder = layerBuilder;
		this.fragmentManager = fragmentManager;
		this.biomeSelection = biomeSelection;
		this.actionsSupplier = actionsSupplier;
		this.threadMaster = threadMaster;
		this.frame = frame;
		this.contentPane = contentPane;
		this.viewerFacadeReference = viewerFacadeReference;
		this.dialogs = dialogs;
		this.menuBarSupplier = menuBarSupplier;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void displayWorld(WorldOptions worldOptions) {
		try {
			clearViewerFacade();
			setWorld(runningLauncherProfile.createWorld(worldOptions));
		} catch (IllegalStateException | MinecraftInterfaceException e) {
			AmidstLogger.warn(e);
			dialogs.displayError(e);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void displayWorld(Path file) {
		try {
			clearViewerFacade();
			setWorld(runningLauncherProfile.createWorldFromSaveGame(minecraftInstallation.newSaveGame(file)));
		} catch (IllegalStateException | MinecraftInterfaceException | IOException | FormatException e) {
			AmidstLogger.warn(e);
			dialogs.displayError(e);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void clearViewerFacade() {
		threadMaster.clearOnRepaintTick();
		threadMaster.clearOnFragmentLoadTick();
		ViewerFacade viewerFacade = viewerFacadeReference.getAndSet(null);
		if (viewerFacade != null) {
			contentPane.remove(viewerFacade.getComponent());
			viewerFacade.dispose();
		}
		menuBarSupplier.get().clear();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void setWorld(World world) {
		if (decideWorldPlayerType(world.getMovablePlayerList())) {
			ViewerFacade v = new PerViewerFacadeInjector(
					settings,
					threadMaster.getWorkerExecutor(),
					zoom,
					layerBuilder,
					fragmentManager,
					biomeExporterDialog,
					biomeSelection,
					world,
					actionsSupplier.get()).getViewerFacade();
			setViewerFacade(v);
		} else {
			frame.revalidate();
			frame.repaint();
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private boolean decideWorldPlayerType(MovablePlayerList movablePlayerList) {
		if (movablePlayerList.getWorldPlayerType().equals(WorldPlayerType.BOTH)) {
			WorldPlayerType worldPlayerType = dialogs.askForWorldPlayerType();
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
		menuBarSupplier.get().set(viewerFacade);
		frame.validate();
		viewerFacade.loadPlayers();
		threadMaster.setOnRepaintTick(viewerFacade.getOnRepainterTick());
		threadMaster.setOnFragmentLoadTick(viewerFacade.getOnFragmentLoaderTick());
		viewerFacadeReference.set(viewerFacade);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void clearWorld() {
		clearViewerFacade();
	}
}
