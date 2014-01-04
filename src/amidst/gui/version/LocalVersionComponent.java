package amidst.gui.version;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;

import MoF.FinderWindow;
import amidst.Options;
import amidst.logging.Log;
import amidst.minecraft.Minecraft;
import amidst.minecraft.MinecraftUtil;
import amidst.version.IProfileUpdateListener;
import amidst.version.MinecraftProfile;
import amidst.version.ProfileUpdateEvent;
import amidst.version.MinecraftProfile.Status;

public class LocalVersionComponent extends VersionComponent {
	protected MinecraftProfile profile;
	protected int oldWidth = 0;
	protected String drawName;
	private String name;
	
	
	public LocalVersionComponent(MinecraftProfile profile) {
		this.profile = profile;
		drawName = profile.getProfileName();
		name = "local:" + profile.getProfileName();
		
		profile.addUpdateListener(new IProfileUpdateListener() {
			@Override
			public void onProfileUpdate(ProfileUpdateEvent event) {
				repaint();
			}
		});
	}
	
	@Override
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
	
	public MinecraftProfile getProfile() {
		return profile;
	}
	
	@Override
	public boolean isReadyToLoad() {
		return profile.getStatus() == Status.FOUND;
	}
	
	@Override
	public void load() {
		isLoading = true;
		repaint();
		Options.instance.lastProfile.set(name);
		(new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					MinecraftUtil.setBiomeInterface(new Minecraft(profile.getJarFile()).createInterface());
					new FinderWindow();
					VersionSelectWindow.get().dispose();
				} catch (MalformedURLException e) {
					Log.crash(e, "MalformedURLException on Minecraft load.");
				}
			}
		})).start();
	}

	@Override
	public String getVersionName() {
		return name;
	}
}
