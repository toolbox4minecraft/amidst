package amidst.gui.crash;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.logging.AmidstLogger;
import net.miginfocom.swing.MigLayout;

@NotThreadSafe
public enum CrashWindow {
	CRASHING("Amidst crashed!", true, () -> {
		System.exit(4);
	}),
	INTEREST("Log Messages", false, () -> {
		// noop
	});

	public static void showAfterCrash() {
		String allMessages = AmidstLogger.getAllMessages();
		SwingUtilities.invokeLater(() -> CRASHING.set(allMessages));
	}

	public static void showForInterest() {
		String allMessages = AmidstLogger.getAllMessages();
		SwingUtilities.invokeLater(() -> INTEREST.set(allMessages));
	}

	private final JLabel pleaseReportLabel;
	private final JTextField pleaseReportTextField;
	private final JTextArea logMessagesTextArea;
	private final JFrame frame;
	private final boolean isCrashing;
	private final Runnable executeOnClose;

	@CalledOnlyBy(AmidstThread.EDT)
	private CrashWindow(String title, boolean isCrashing, Runnable executeOnClose) {
		this.isCrashing = isCrashing;
		this.executeOnClose = executeOnClose;
		if (isCrashing) {
			this.pleaseReportLabel = new JLabel("Please report this bug on:");
			this.pleaseReportTextField = createReportingTextField();
		} else {
			this.pleaseReportLabel = null;
			this.pleaseReportTextField = null;
		}
		this.logMessagesTextArea = createLogMessagesTextArea();
		this.frame = createFrame(title);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private JScrollPane createLogMessagesScrollPane(JTextArea textArea) {
		JScrollPane result = new JScrollPane(textArea);
		result.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		result.setBorder(new LineBorder(Color.darkGray, 1));
		return result;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private JFrame createFrame(String title) {
		JFrame result = new JFrame(title);
		result.getContentPane().setLayout(new MigLayout());
		if (isCrashing) {
			result.add(pleaseReportLabel, "growx, pushx, wrap");
			result.add(pleaseReportTextField, "growx, pushx, wrap");
		}
		result.add(createLogMessagesScrollPane(logMessagesTextArea), "grow, push");
		result.setSize(800, 600);
		result.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				result.setVisible(false);
				executeOnClose.run();
			}
		});
		return result;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private JTextField createReportingTextField() {
		JTextField result = new JTextField("https://github.com/toolbox4minecraft/amidst/issues/new");
		result.setEditable(false);
		result.selectAll();
		return result;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private JTextArea createLogMessagesTextArea() {
		JTextArea result = new JTextArea();
		result.setEditable(false);
		result.setFont(new Font("arial", Font.PLAIN, 10));
		return result;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void set(String logMessages) {
		if (isCrashing) {
			this.pleaseReportTextField.selectAll();
		}
		this.logMessagesTextArea.setText(logMessages);
		this.frame.setVisible(true);
	}
}
