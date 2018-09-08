package amidst.gui.main;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import amidst.dependency.injection.Factory1;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.gui.main.menu.AmidstMenu;
import amidst.gui.main.viewer.ViewerFacade;
import amidst.gameengineabstraction.file.IGameInstallation;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.RunningLauncherProfile;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.WorldSeed;
import amidst.mojangapi.world.WorldType;
import amidst.mojangapi.world.player.MovablePlayerList;
import amidst.mojangapi.world.player.WorldPlayerType;
import amidst.parsing.FormatException;
import amidst.settings.biomeprofile.BiomeProfileSelection;
import amidst.threading.ThreadMaster;

@NotThreadSafe
public class WorldSwitcher {
	private final IGameInstallation minecraftInstallation;
	private final RunningLauncherProfile runningLauncherProfile;
	private final Factory1<World, ViewerFacade> viewerFacadeFactory;
	private final ThreadMaster threadMaster;
	private final JFrame frame;
	private final Container contentPane;
	private final AtomicReference<ViewerFacade> viewerFacadeReference;
	private final MainWindowDialogs dialogs;
	private final Supplier<AmidstMenu> menuBarSupplier;
	private final ArrayList<WorldSwitchedListener> listeners = new ArrayList<WorldSwitchedListener>();

	@CalledOnlyBy(AmidstThread.EDT)
	public WorldSwitcher(
			IGameInstallation minecraftInstallation,
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
	
	public ViewerFacade addWorldSwitchedListener(WorldSwitchedListener listener) {
		listeners.add(listener);
		return viewerFacadeReference.get();
	}
	public void removeWorldSwitchedListener(WorldSwitchedListener listener) {
		listeners.remove(listener);
	}	

	@CalledOnlyBy(AmidstThread.EDT)
	public void displayWorld(WorldSeed worldSeed, WorldType worldType, BiomeProfileSelection biomeProfileSelection) {
		try {
			clearViewerFacade();
			setWorld(runningLauncherProfile.createWorldFromSeed(worldSeed, worldType, biomeProfileSelection));
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
		} catch (IllegalStateException | MinecraftInterfaceException | IOException | FormatException | UnsupportedOperationException e) {
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
		
		SwingUtilities.invokeLater(() -> {
			for (WorldSwitchedListener listener: listeners) {
				listener.onWorldSwitched(viewerFacade);
			}		
		});		
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void clearWorld() {
		clearViewerFacade();
	}
}
