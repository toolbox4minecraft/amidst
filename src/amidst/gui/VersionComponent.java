package amidst.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;

import amidst.version.MinecraftVersion;

public class VersionComponent extends JComponent {
	private MinecraftVersion version;
	public VersionComponent(MinecraftVersion version) {
		this.setMinimumSize(new Dimension(300, 40));
	}
	
	public void paintComponent(Graphics g) {
		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.black);
		g.drawRect(0, 0, getWidth()-1, getHeight()-1);
		
	}
}
