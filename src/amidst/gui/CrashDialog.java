package amidst.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

import amidst.logging.LogRecorder;

import net.miginfocom.swing.MigLayout;

public class CrashDialog extends JFrame {
	private static final long serialVersionUID = 2016466949762866300L;

	public CrashDialog(String message) {
		super("AMIDST encountered an unexpected error.");
		Container contentPane = getContentPane();
		contentPane.setLayout(new MigLayout());

		JLabel errorText = new JLabel("AMIDST has crashed with the following message: " + message);
		add(errorText, "w 0:4000:4000, wrap");
		JTextArea logText = new JTextArea(LogRecorder.getContents());

		JScrollPane scrollPane = new JScrollPane(logText);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		logText.setFont(new Font("arial", Font.PLAIN, 10));
		scrollPane.setBorder(new LineBorder(Color.darkGray, 1));
		
		add(scrollPane,"w 0:4000:4000");
		
		setSize(500, 400);
		setVisible(true);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e)
			{
				dispose();
				System.exit(4);
			}
		});
		
	}
}
