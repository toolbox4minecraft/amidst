package amidst.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

public class VersionSelectPanel extends JPanel {
	public VersionSelectPanel() {
		setLayout(new MigLayout("novisualpadding"));
		//this.setMinimumSize(new Dimension(50, 50));
	}
	
	public void addVersion(VersionComponent version) {
		add(version, "dock top");
	}
	
	public void paintComponent(Graphics g) {
		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.black);
		g.drawRect(0, 0, getWidth()-1, getHeight()-1);
	}
}
