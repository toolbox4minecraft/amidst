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
import javax.swing.border.LineBorder;

import net.miginfocom.swing.MigLayout;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;

@NotThreadSafe
public enum CrashWindow {
	INSTANCE;

	@CalledOnlyBy(AmidstThread.EDT)
	public static void show(String message, String logMessages, Runnable executeOnClose) {
		INSTANCE.set(message, logMessages, executeOnClose);
	}

	private final JLabel messageLabel;
	private final JTextArea logMessagesTextArea;
	private final JFrame frame;
	private volatile Runnable executeOnClose;

	@CalledOnlyBy(AmidstThread.EDT)
	private CrashWindow() {
		this.messageLabel = createMessageLabel();
		this.logMessagesTextArea = createLogMessagesTextArea();
		this.frame = createFrame();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private JLabel createMessageLabel() {
		return new JLabel();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private JScrollPane createLogMessagesScrollPane(JTextArea textArea) {
		JScrollPane result = new JScrollPane(textArea);
		result.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		result.setBorder(new LineBorder(Color.darkGray, 1));
		return result;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private JFrame createFrame() {
		JFrame result = new JFrame("Amidst crashed!");
		result.getContentPane().setLayout(new MigLayout());
		result.add(messageLabel, "growx, pushx, wrap");
		result.add(new JLabel("Please report this bug on:"), "growx, pushx, wrap");
		result.add(createReportingTextField(), "growx, pushx, wrap");
		result.add(createLogMessagesScrollPane(logMessagesTextArea), "grow, push");
		result.setSize(800, 600);
		result.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				result.dispose();
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
	public void set(String message, String logMessages, Runnable executeOnClose) {
		this.messageLabel.setText(message);
		this.logMessagesTextArea.setText(logMessages);
		this.executeOnClose = executeOnClose;
		this.frame.setVisible(true);
	}
}
