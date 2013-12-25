package amidst.gui.version;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

import amidst.version.MinecraftVersion;

public class VersionComponent extends JComponent {
	private static Font nameFont = new Font("arial", Font.BOLD, 30);
	
	private MinecraftVersion version;
	
	public VersionComponent(MinecraftVersion version) {
		this.setMinimumSize(new Dimension(300, 40));
		this.version = version;
	}
	
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		g2d.setColor(Color.white);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		g2d.setColor(Color.black);
		g2d.drawRect(0, 0, getWidth()-1, getHeight()-1);
		
		g2d.setFont(nameFont);
		g2d.drawString(version.getShortName(), 5, 30);
		
	}
}
