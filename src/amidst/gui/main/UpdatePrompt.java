package amidst.gui.main;

import java.awt.Desktop;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;

public class UpdatePrompt {
	private UpdateInformationRetriever retriever = new UpdateInformationRetriever();
	private MainWindow mainWindow;
	private boolean silent;

	@CalledOnlyBy(AmidstThread.EDT)
	public void checkSilently(MainWindow mainWindow) {
		check(mainWindow, true);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void check(MainWindow mainWindow) {
		check(mainWindow, false);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void check(MainWindow mainWindow, boolean silent) {
		this.mainWindow = mainWindow;
		this.silent = silent;
		try {
			doCheck();
		} catch (MalformedURLException e) {
			error("Error connecting to update server: Malformed URL.");
		} catch (IOException e) {
			error("Error reading update data.");
		} catch (URISyntaxException e) {
			error("Error parsing update URL.");
		} catch (NullPointerException e) {
			error("Error \"NullPointerException\" in update.");
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void error(String message) {
		if (!silent) {
			mainWindow.displayError(message);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void doCheck() throws IOException, URISyntaxException {
		retriever.check();
		if (!retriever.isSuccessful()) {
			error(retriever.getErrorMessage());
		} else if (getUserChoice()) {
			openUpdateURL();
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private boolean getUserChoice() {
		if (retriever.isNewMajorVersionAvailable()) {
			return mainWindow.askToConfirm("Update Found",
					"A new version was found. Would you like to update?");
		} else if (retriever.isNewMinorVersionAvailable()) {
			return mainWindow.askToConfirm("Update Found",
					"A minor revision was found. Update?");
		} else if (!silent) {
			mainWindow.displayMessage("Updater", "There are no new updates.");
		}
		return false;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void openUpdateURL() throws IOException, URISyntaxException {
		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			if (desktop.isSupported(Desktop.Action.BROWSE)) {
				desktop.browse(new URI(retriever.getUpdateURL()));
			} else {
				mainWindow.displayError("Unable to open browser page.");
			}
		} else {
			mainWindow.displayError("Unable to open browser.");
		}
	}
}
