package amidst.gui.main;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Consumer;
import java.util.function.Function;

import amidst.AmidstVersion;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotNull;
import amidst.documentation.NotThreadSafe;
import amidst.logging.AmidstLogger;
import amidst.logging.AmidstMessageBox;
import amidst.threading.WorkerExecutor;

@NotThreadSafe
public class UpdatePrompt {
	@CalledOnlyBy(AmidstThread.EDT)
	@NotNull
	public static UpdatePrompt from(
			AmidstVersion currentVersion,
			WorkerExecutor workerExecutor,
			MainWindowDialogs dialogs,
			boolean silent) {
		if (dialogs != null) {
			if (silent) {
				return new UpdatePrompt(
						currentVersion,
						workerExecutor,
						NOOP_CONSUMER,
						NOOP,
						message -> dialogs.askToConfirmYesNo(TITLE, message));
			} else {
				return new UpdatePrompt(
						currentVersion,
						workerExecutor,
						e -> dialogs.displayError(e),
						() -> dialogs.displayInfo(TITLE, NO_UPDATES_AVAILABLE),
						message -> dialogs.askToConfirmYesNo(TITLE, message));
			}
		} else {
			if (silent) {
				return new UpdatePrompt(
						currentVersion,
						workerExecutor,
						NOOP_CONSUMER,
						NOOP,
						message -> AmidstMessageBox.askToConfirmYesNo(TITLE, message));
			} else {
				return new UpdatePrompt(
						currentVersion,
						workerExecutor,
						e -> AmidstMessageBox.displayError(TITLE, e),
						() -> AmidstMessageBox.displayInfo(TITLE, NO_UPDATES_AVAILABLE),
						message -> AmidstMessageBox.askToConfirmYesNo(TITLE, message));
			}
		}
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
		AmidstLogger.warn(e, "unable to check for updates");
		exceptionConsumer.accept(e);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void displayResult(UpdateInformationJson updateInformation) {
		if (getUserChoice(updateInformation)) {
			try {
				openURL(new URI(updateInformation.getDownloadUrl()));
			} catch (IOException | UnsupportedOperationException | URISyntaxException e) {
				AmidstLogger.warn(e);
				exceptionConsumer.accept(e);
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
		} else if (newVersion.isNewerPatchVersionThan(currentVersion)) {
			return askToConfirm(createMessage(message, newVersion, "patch"));
		} else if (newVersion.isSameVersionButOldPreReleaseAndNewStable(currentVersion)) {
			return askToConfirm(createMessage(message, newVersion, "stable"));
		} else {
			AmidstLogger.info(NO_UPDATES_AVAILABLE);
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
