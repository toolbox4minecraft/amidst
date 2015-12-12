package amidst.gui.main;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.Immutable;
import amidst.logging.Log;

@Immutable
public class UpdatePrompt {
	private final MainWindow mainWindow;

	public UpdatePrompt(MainWindow mainWindow) {
		this.mainWindow = mainWindow;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void checkSilently() {
		check(true);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void check() {
		check(false);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void check(boolean silent) {
		try {
			doCheck(silent);
		} catch (Exception e) {
			Log.w("unable to check for updates");
			e.printStackTrace();
			if (!silent) {
				mainWindow.displayError(e.getMessage());
			}
		}
	}

	private void doCheck(boolean silent) throws Exception, IOException,
			URISyntaxException {
		UpdateInformationRetriever retriever = new UpdateInformationRetriever();
		if (getUserChoice(retriever, silent)) {
			openURL(new URI(retriever.getUpdateURL()));
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private boolean getUserChoice(UpdateInformationRetriever retriever,
			boolean silent) {
		if (retriever.isNewMajorVersionAvailable()) {
			return mainWindow.askToConfirm("Update Found",
					"A new version was found. Would you like to update?");
		} else if (retriever.isNewMinorVersionAvailable()) {
			return mainWindow.askToConfirm("Update Found",
					"A minor revision was found. Update?");
		} else if (silent) {
			return false;
		} else {
			mainWindow.displayMessage("Updater", "There are no new updates.");
			return false;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void openURL(URI uri) throws IOException {
		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			if (desktop.isSupported(Desktop.Action.BROWSE)) {
				desktop.browse(uri);
			} else {
				throw new RuntimeException("Unable to open browser page.");
			}
		} else {
			throw new RuntimeException("Unable to open browser.");
		}
	}
}
