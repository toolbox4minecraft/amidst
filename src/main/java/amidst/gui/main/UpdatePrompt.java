package amidst.gui.main;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import amidst.AmidstVersion;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.logging.Log;
import amidst.threading.SimpleWorker;
import amidst.threading.WorkerExecutor;

@NotThreadSafe
public class UpdatePrompt {
	private static final String TITLE = "Amidst Updater";

	private final AmidstVersion currentVersion;
	private final MainWindow mainWindow;
	private final WorkerExecutor workerExecutor;

	@CalledOnlyBy(AmidstThread.EDT)
	public UpdatePrompt(AmidstVersion currentVersion, MainWindow mainWindow,
			WorkerExecutor workerExecutor) {
		this.currentVersion = currentVersion;
		this.mainWindow = mainWindow;
		this.workerExecutor = workerExecutor;
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
	private void check(final boolean silent) {
		workerExecutor.invokeLater(new SimpleWorker<UpdateInformationJson>() {
			@Override
			protected UpdateInformationJson main() throws IOException {
				return UpdateInformationRetriever.retrieve();
			}

			@Override
			protected void onMainFinished(
					UpdateInformationJson updateInformation) {
				displayResult(silent, updateInformation);
			}

			@Override
			protected void onMainFinishedWithException(Exception e) {
				Log.w("unable to check for updates");
				displayError(silent, e);
			}
		});
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void displayError(boolean silent, Exception e) {
		e.printStackTrace();
		if (!silent) {
			mainWindow.displayException(e);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void displayResult(boolean silent,
			UpdateInformationJson updateInformation) {
		if (getUserChoice(updateInformation, silent)) {
			try {
				openURL(new URI(updateInformation.getDownloadUrl()));
			} catch (IOException | UnsupportedOperationException
					| URISyntaxException e) {
				displayError(silent, e);
			}
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private boolean getUserChoice(UpdateInformationJson updateInformation,
			boolean silent) {
		AmidstVersion newVersion = updateInformation.createAmidstVersion();
		String message = updateInformation.getMessage();
		if (newVersion.isNewerMajorVersionThan(currentVersion)) {
			return askToConfirm(createMessage(message, newVersion, "major"));
		} else if (newVersion.isNewerMinorVersionThan(currentVersion)) {
			return askToConfirm(createMessage(message, newVersion, "minor"));
		} else if (newVersion
				.isSameVersionButOldPreReleaseAndNewStable(currentVersion)) {
			return askToConfirm(createMessage(message, newVersion, "stable"));
		} else if (silent) {
			return false;
		} else {
			mainWindow.displayMessage(TITLE, "There are no updates available.");
			return false;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private String createMessage(String message, AmidstVersion newVersion,
			String versionType) {
		return "A new " + versionType + " version of Amidst is available:\n"
				+ "Current Version: " + currentVersion.createVersionString()
				+ "\n" + "New Version: " + newVersion.createVersionString()
				+ "\n" + "Do you want to upgrade?"
				+ createMessageSuffix(message);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private String createMessageSuffix(String message) {
		if (message != null && !message.isEmpty()) {
			return "\n\n" + message;
		} else {
			return "";
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private boolean askToConfirm(String message) {
		return mainWindow.askToConfirm(TITLE, message);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void openURL(URI uri) throws IOException,
			UnsupportedOperationException {
		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			if (desktop.isSupported(Desktop.Action.BROWSE)) {
				desktop.browse(uri);
			} else {
				throw new UnsupportedOperationException(
						"Unable to open browser page.");
			}
		} else {
			throw new UnsupportedOperationException("Unable to open browser.");
		}
	}
}
