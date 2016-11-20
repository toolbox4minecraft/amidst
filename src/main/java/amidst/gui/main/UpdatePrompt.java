package amidst.gui.main;

import java.awt.Desktop;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.swing.JOptionPane;

import amidst.AmidstVersion;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotNull;
import amidst.documentation.NotThreadSafe;
import amidst.logging.Log;
import amidst.threading.WorkerExecutor;

@NotThreadSafe
public class UpdatePrompt {
	@CalledOnlyBy(AmidstThread.EDT)
	@NotNull
	public static UpdatePrompt from(
			AmidstVersion currentVersion,
			WorkerExecutor workerExecutor,
			MainWindow mainWindow,
			boolean silent) {
		if (mainWindow != null) {
			if (silent) {
				return new UpdatePrompt(
						currentVersion,
						workerExecutor,
						NOOP_CONSUMER,
						NOOP,
						message -> mainWindow.askToConfirm(TITLE, message));
			} else {
				return new UpdatePrompt(
						currentVersion,
						workerExecutor,
						exception -> mainWindow.displayException(exception),
						() -> mainWindow.displayMessage(TITLE, NO_UPDATES_AVAILABLE),
						message -> mainWindow.askToConfirm(TITLE, message));
			}
		} else {
			if (silent) {
				return new UpdatePrompt(
						currentVersion,
						workerExecutor,
						NOOP_CONSUMER,
						NOOP,
						message -> askToConfirmDirectly(message));
			} else {
				return new UpdatePrompt(
						currentVersion,
						workerExecutor,
						exception -> displayExceptionDirectly(exception),
						() -> displayMessageDirectly(NO_UPDATES_AVAILABLE),
						message -> askToConfirmDirectly(message));
			}
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private static void displayExceptionDirectly(Exception exception) {
		JOptionPane.showMessageDialog(null, getStackTraceAsString(exception), TITLE, JOptionPane.ERROR_MESSAGE);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private static String getStackTraceAsString(Exception exception) {
		StringWriter writer = new StringWriter();
		exception.printStackTrace(new PrintWriter(writer));
		return writer.toString();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private static void displayMessageDirectly(String message) {
		JOptionPane.showMessageDialog(null, message, TITLE, JOptionPane.INFORMATION_MESSAGE);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private static boolean askToConfirmDirectly(String message) {
		return JOptionPane.showConfirmDialog(null, message, TITLE, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
	}

	private static final String TITLE = "Amidst Updater";
	private static final String NO_UPDATES_AVAILABLE = "There are no updates available.";

	private static final Runnable NOOP = () -> {
	};
	private static final Consumer<Exception> NOOP_CONSUMER = e -> {
	};

	private final AmidstVersion currentVersion;
	private final WorkerExecutor workerExecutor;
	private final Consumer<Exception> exceptionConsumer;
	private final Runnable noUpdatesDisplayer;
	private final Function<String, Boolean> updateConfirmer;

	@CalledOnlyBy(AmidstThread.EDT)
	public UpdatePrompt(
			AmidstVersion currentVersion,
			WorkerExecutor workerExecutor,
			Consumer<Exception> exceptionConsumer,
			Runnable noUpdatesDisplayer,
			Function<String, Boolean> updateConfirmer) {
		this.currentVersion = currentVersion;
		this.workerExecutor = workerExecutor;
		this.exceptionConsumer = exceptionConsumer;
		this.noUpdatesDisplayer = noUpdatesDisplayer;
		this.updateConfirmer = updateConfirmer;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void check() {
		workerExecutor.run(UpdateInformationRetriever::retrieve, this::displayResult, this::onCheckFailed);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void onCheckFailed(Exception e) {
		Log.w("unable to check for updates");
		displayError(e);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void displayError(Exception e) {
		e.printStackTrace();
		exceptionConsumer.accept(e);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void displayResult(UpdateInformationJson updateInformation) {
		if (getUserChoice(updateInformation)) {
			try {
				openURL(new URI(updateInformation.getDownloadUrl()));
			} catch (IOException | UnsupportedOperationException | URISyntaxException e) {
				displayError(e);
			}
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private boolean getUserChoice(UpdateInformationJson updateInformation) {
		AmidstVersion newVersion = updateInformation.createAmidstVersion();
		String message = updateInformation.getMessage();
		if (newVersion.isNewerMajorVersionThan(currentVersion)) {
			return askToConfirm(createMessage(message, newVersion, "major"));
		} else if (newVersion.isNewerMinorVersionThan(currentVersion)) {
			return askToConfirm(createMessage(message, newVersion, "minor"));
		} else if (newVersion.isSameVersionButOldPreReleaseAndNewStable(currentVersion)) {
			return askToConfirm(createMessage(message, newVersion, "stable"));
		} else {
			noUpdatesDisplayer.run();
			return false;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private String createMessage(String message, AmidstVersion newVersion, String versionType) {
		return "A new " + versionType + " version of Amidst is available:\n" + "Current Version: "
				+ currentVersion.createVersionString() + "\n" + "New Version: " + newVersion.createVersionString()
				+ "\n" + "Do you want to upgrade?" + createMessageSuffix(message);
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
		return updateConfirmer.apply(message);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void openURL(URI uri) throws IOException, UnsupportedOperationException {
		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			if (desktop.isSupported(Desktop.Action.BROWSE)) {
				desktop.browse(uri);
			} else {
				throw new UnsupportedOperationException("Unable to open browser page.");
			}
		} else {
			throw new UnsupportedOperationException("Unable to open browser.");
		}
	}
}
