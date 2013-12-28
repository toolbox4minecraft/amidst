package amidst.gui.version;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;

import javax.swing.JComponent;

import MoF.FinderWindow;
import amidst.Options;
import amidst.logging.Log;
import amidst.minecraft.Minecraft;
import amidst.resources.ResourceLoader;
import amidst.version.IProfileUpdateListener;
import amidst.version.MinecraftProfile;
import amidst.version.MinecraftProfile.Status;
import amidst.version.MinecraftVersion;
import amidst.version.ProfileUpdateEvent;

public class VersionComponent extends JComponent {
	private static Font nameFont = new Font("arial", Font.BOLD, 30);
	private static Font statusFont = new Font("arial", Font.BOLD, 10);
	private static Font versionFont = new Font("arial", Font.BOLD, 16);
	private static BufferedImage activeIcon   = ResourceLoader.getImage("active_profile.png");
	private static BufferedImage inactiveIcon = ResourceLoader.getImage("inactive_profile.png");
	private static BufferedImage loadingIcon  = ResourceLoader.getImage("loading_profile.png");
	private static Color selectedBgColor = new Color(160, 190, 255);
	private static Color loadingBgColor = new Color(112, 203, 91);
	
	private MinecraftProfile profile;
	private int oldWidth = 0;
	private String drawName;
	
	private boolean selected = false;
	private boolean isLoading = false;
	
	public VersionComponent(MinecraftProfile profile) {
		this.setMinimumSize(new Dimension(300, 40));
		this.setPreferredSize(new Dimension(500, 40));
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
		
		if (isLoading)
			g2d.setColor(loadingBgColor);
		else if (isSelected())
			g2d.setColor(selectedBgColor);
		else
			g2d.setColor(Color.white);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		
		g2d.setColor(Color.black);
		g2d.setFont(versionFont);
		fontMetrics = g2d.getFontMetrics();
		int versionNameX = getWidth() - 40 - fontMetrics.stringWidth(profile.getVersionName());
		g2d.drawString(profile.getVersionName(), versionNameX, 20);
		
		g2d.setColor(Color.black);
		g2d.setFont(nameFont);
		if (oldWidth != getWidth()) {
			fontMetrics = g2d.getFontMetrics();
			String name = profile.getProfileName();
			if (fontMetrics.stringWidth(name) > versionNameX - 25) {
				int widthSum = 0;
				for (int i = 0; i < name.length(); i++) {
					widthSum += fontMetrics.charWidth(name.charAt(i));
					if (widthSum > versionNameX - 25) {
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
		g2d.drawString(statusString, getWidth() - 40 - fontMetrics.stringWidth(statusString), 32);
		
		BufferedImage image = inactiveIcon;
		if (isLoading)
			image = loadingIcon;
		else if (profile.getStatus() == Status.FOUND)
			image = activeIcon;
		g2d.drawImage(image, getWidth() - image.getWidth() - 5, 4, null);
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
	
	public MinecraftProfile getProfile() {
		return profile;
	}
	
	public void load() {
		isLoading = true;
		repaint();
		Options.instance.getPreferences().put("profile", profile.getProfileName());
		(new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					new Minecraft(profile.getJarFile());
					new FinderWindow();
					VersionSelectWindow.get().dispose();
				} catch (MalformedURLException e) {
					Log.crash(e, "MalformedURLException on Minecraft load.");
				}
			}
		})).start();
	}
}
