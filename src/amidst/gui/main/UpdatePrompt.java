package amidst.gui.main;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.logging.Log;
import amidst.threading.SimpleWorker;
import amidst.threading.WorkerExecutor;

@NotThreadSafe
public class UpdatePrompt {
	private final MainWindow mainWindow;
	private final WorkerExecutor workerExecutor;

	@CalledOnlyBy(AmidstThread.EDT)
	public UpdatePrompt(MainWindow mainWindow, WorkerExecutor workerExecutor) {
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
		workerExecutor
				.invokeLater(new SimpleWorker<UpdateInformationRetriever>() {
					@Override
					protected UpdateInformationRetriever main()
							throws Exception {
						return new UpdateInformationRetriever();
					}

					@Override
					protected void onMainFinished(
							UpdateInformationRetriever retriever) {
						displayResult(silent, retriever);
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
			mainWindow.displayError(e.getMessage());
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void displayResult(boolean silent,
			UpdateInformationRetriever retriever) {
		if (getUserChoice(retriever, silent)) {
			try {
				openURL(new URI(retriever.getUpdateURL()));
			} catch (Exception e) {
				displayError(silent, e);
			}
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
