package amidst.gui.main;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import javax.swing.JFrame;

import amidst.dependency.injection.Factory1;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.gui.main.menu.AmidstMenu;
import amidst.gui.main.viewer.ViewerFacade;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.RunningLauncherProfile;
import amidst.mojangapi.file.MinecraftInstallation;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.WorldSeed;
import amidst.mojangapi.world.WorldType;
import amidst.mojangapi.world.player.MovablePlayerList;
import amidst.mojangapi.world.player.WorldPlayerType;
import amidst.parsing.FormatException;
import amidst.threading.ThreadMaster;

@NotThreadSafe
public class WorldSwitcher {
	private final MinecraftInstallation minecraftInstallation;
	private final RunningLauncherProfile runningLauncherProfile;
	private final Factory1<World, ViewerFacade> viewerFacadeFactory;
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
			Factory1<World, ViewerFacade> viewerFacadeFactory,
			ThreadMaster threadMaster,
			JFrame frame,
			Container contentPane,
			AtomicReference<ViewerFacade> viewerFacadeReference,
			MainWindowDialogs dialogs,
			Supplier<AmidstMenu> menuBarSupplier) {
		this.minecraftInstallation = minecraftInstallation;
		this.runningLauncherProfile = runningLauncherProfile;
		this.viewerFacadeFactory = viewerFacadeFactory;
		this.threadMaster = threadMaster;
		this.frame = frame;
		this.contentPane = contentPane;
		this.viewerFacadeReference = viewerFacadeReference;
		this.dialogs = dialogs;
		this.menuBarSupplier = menuBarSupplier;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void displayWorld(WorldSeed worldSeed, WorldType worldType) {
		try {
			clearViewerFacade();
			setWorld(runningLauncherProfile.createWorldFromSeed(worldSeed, worldType));
		} catch (IllegalStateException | MinecraftInterfaceException e) {
			AmidstLogger.warn(e);
			dialogs.displayError(e);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void displayWorld(File file) {
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
			setViewerFacade(viewerFacadeFactory.create(world));
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
