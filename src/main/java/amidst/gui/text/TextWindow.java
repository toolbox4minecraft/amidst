package amidst.gui.text;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import net.miginfocom.swing.MigLayout;

@NotThreadSafe
public class TextWindow {
	private static final TextWindow monospaceWindow = new TextWindow(Font.MONOSPACED);

	public static void showMonospace(Component parent, String title, String content) {
		SwingUtilities.invokeLater(() -> monospaceWindow.show(parent, title, content));
	}

	private final JFrame frame;
	private final JTextArea contentTextArea;

	@CalledOnlyBy(AmidstThread.EDT)
	private TextWindow(String contentFont) {
		this.contentTextArea = createContentTextArea(contentFont);
		this.frame = createFrame("Details:");
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private JScrollPane createContentScrollPane(JTextArea textArea) {
		JScrollPane result = new JScrollPane(textArea);
		result.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		result.setBorder(new LineBorder(Color.darkGray, 1));
		return result;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private JFrame createFrame(String title) {
		JFrame result = new JFrame(title);
		result.getContentPane().setLayout(new MigLayout());
		result.add(createContentScrollPane(contentTextArea), "grow, push");
		result.setSize(600, 700);
		result.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				result.setVisible(false);
			}
		});
		return result;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private JTextArea createContentTextArea(String contentFont) {
		JTextArea result = new JTextArea();
		result.setEditable(false);
		result.setFont(new Font(contentFont, Font.PLAIN, 11));
		return result;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void show(Component parent, String title, String content) {
		this.contentTextArea.setText(content);
		this.frame.setTitle(title);
		this.frame.setLocationRelativeTo(parent);
		this.frame.setVisible(true);
	}
}
