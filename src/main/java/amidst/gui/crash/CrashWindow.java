package amidst.gui.crash;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.LineBorder;

import net.miginfocom.swing.MigLayout;
import amidst.documentation.Immutable;

@Immutable
public class CrashWindow {
	private final JFrame frame;

	public CrashWindow(String message, String logMessages,
			final Runnable executeOnClose) {
		frame = new JFrame("Amidst encountered an unexpected error.");
		frame.getContentPane().setLayout(new MigLayout());
		frame.add(new JLabel("Amidst has crashed with the following message:"),
				"growx, pushx, wrap");
		frame.add(new JLabel(message), "growx, pushx, wrap");
		frame.add(createLogMessagesScrollPane(logMessages), "grow, push");
		frame.setSize(500, 400);
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				frame.dispose();
				executeOnClose.run();
			}
		});
	}

	private JScrollPane createLogMessagesScrollPane(String logMessages) {
		JScrollPane result = new JScrollPane(
				createLogMessagesTextArea(logMessages));
		result.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		result.setBorder(new LineBorder(Color.darkGray, 1));
		return result;
	}

	private JTextArea createLogMessagesTextArea(String logMessages) {
		JTextArea result = new JTextArea(logMessages);
		result.setEditable(false);
		result.setFont(new Font("arial", Font.PLAIN, 10));
		return result;
	}
}
