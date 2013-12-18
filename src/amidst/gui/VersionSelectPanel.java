package amidst.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.HashMap;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

public class VersionSelectPanel extends JPanel {
	private String emptyMessage;
	private int emptyMessageWidth;
	private FontMetrics emptyMessageMetric;
	private Font emptyMessageFont = new Font("arial", Font.BOLD, 30);
	
	private HashMap<String, VersionComponent> componentMap = new HashMap<String, VersionComponent>();
	
	public VersionSelectPanel() {
		setLayout(new MigLayout("ins 0", "", "[]0[]"));
		setEmptyMessage("Empty");
		//this.setMinimumSize(new Dimension(50, 50));
	}
	
	public void addVersion(VersionComponent version) {
		add(version, "growx, pushx, wrap");
		componentMap.put(version.getName(), version);
	}
	
	public void paintComponent(Graphics g) {
		((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		if (emptyMessageMetric == null) {
			emptyMessageMetric = g.getFontMetrics(emptyMessageFont);
			emptyMessageWidth = emptyMessageMetric.stringWidth(emptyMessage);
		}
		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		if (componentMap.size() == 0) {
			g.setColor(Color.gray);
			g.setFont(emptyMessageFont);
			g.drawString(emptyMessage, (getWidth() >> 1) - (emptyMessageWidth >> 1), 30);
		}
		
	}

	public void setEmptyMessage(String message) {
		emptyMessage = message;
		if (emptyMessageMetric != null)
			emptyMessageWidth = emptyMessageMetric.stringWidth(emptyMessage);
	}
}
