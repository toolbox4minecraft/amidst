package amidst.gui.version;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import amidst.resources.ResourceLoader;
import amidst.version.IProfileUpdateListener;
import amidst.version.MinecraftProfile;
import amidst.version.MinecraftVersion;
import amidst.version.ProfileUpdateEvent;

public class VersionComponent extends JComponent {
	private static Font nameFont = new Font("arial", Font.BOLD, 30);
	private static Font statusFont = new Font("arial", Font.BOLD, 10);
	private static Font versionFont = new Font("arial", Font.BOLD, 16);
	private static BufferedImage inactiveImage = ResourceLoader.getImage("inactive_profile.png");
	private static Color selectedBgColor = new Color(160, 190, 255);
	
	private MinecraftProfile profile;
	private int oldWidth = 0;
	private String drawName;
	
	private boolean selected = false;
	
	public VersionComponent(MinecraftProfile profile) {
		this.setMinimumSize(new Dimension(300, 40));
		this.profile = profile;
		drawName = profile.getProfileName();
		
		
		profile.addUpdateListener(new IProfileUpdateListener() {
			@Override
			public void onProfileUpdate(ProfileUpdateEvent event) {
				repaint();
			}
		});
	}
	
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		FontMetrics fontMetrics = null;
		if (isSelected())
			g2d.setColor(selectedBgColor);
		else
			g2d.setColor(Color.white);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		g2d.setColor(Color.black);
		g2d.setFont(nameFont);
		if (oldWidth != getWidth()) {
			fontMetrics = g2d.getFontMetrics();
			String name = profile.getProfileName();
			if (fontMetrics.stringWidth(name) > getWidth() - 160) {
				int widthSum = 0;
				for (int i = 0; i < name.length(); i++) {
					widthSum += fontMetrics.charWidth(name.charAt(i));
					if (widthSum > getWidth() - 160) {
						name = name.substring(0, i) + "...";
						break;
					}
				}
			}
			drawName = name;
			oldWidth = getWidth();
		}
		g2d.drawString(drawName, 5, 30);
		
		g2d.setColor(Color.gray);
		g2d.setFont(statusFont);
		fontMetrics = g2d.getFontMetrics();
		String statusString = profile.getStatus().toString();
		g2d.drawString(statusString, getWidth() - 50 - fontMetrics.stringWidth(statusString), 32);
		
		g2d.setColor(Color.black);
		g2d.setFont(versionFont);
		fontMetrics = g2d.getFontMetrics();
		g2d.drawString(profile.getVersionName(), getWidth() - 50 - fontMetrics.stringWidth(profile.getVersionName()), 20);
		
		g2d.drawImage(inactiveImage, getWidth() - inactiveImage.getWidth() - 5, 4, null);
	}
	
	public String getProfileName() {
		return profile.getProfileName();
	}
	
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean value) {
		selected = value;
	}
}
