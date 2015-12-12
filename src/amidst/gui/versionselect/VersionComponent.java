package amidst.gui.versionselect;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import amidst.resources.ResourceLoader;

public abstract class VersionComponent {
	@SuppressWarnings("serial")
	private class Component extends JComponent {
		private final FontMetrics versionFontMetrics = getFontMetrics(VERSION_NAME_FONT);
		private final FontMetrics profileFontMetrics = getFontMetrics(PROFILE_NAME_FONT);

		private int versionNameX;
		private int oldWidth;
		private String oldVersionName;
		private String oldProfileName;

		public Component() {
			this.setMinimumSize(new Dimension(300, 40));
			this.setPreferredSize(new Dimension(500, 40));
		}

		@Override
		public void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			drawBackground(g2d);
			updateIfNecessary();
			drawVersionName(g2d);
			drawProfileName(g2d);
			drawStatus(g2d);
			drawIcon(g2d);
		}

		private void drawBackground(Graphics2D g2d) {
			if (isLoading) {
				g2d.setColor(LOADING_BG_COLOR);
			} else if (isSelected()) {
				g2d.setColor(SELECTED_BG_COLOR);
			} else {
				g2d.setColor(DEFAULT_BG_COLOR);
			}
			g2d.fillRect(0, 0, getWidth(), getHeight());
		}

		private void updateIfNecessary() {
			int width = getWidth();
			String versionName = getVersionName();
			String profileName = getProfileName();
			if (oldVersionName == null || oldWidth != width
					|| !oldVersionName.equals(versionName)) {
				versionNameX = width - 40
						- versionFontMetrics.stringWidth(versionName);
				oldWidth = width;
				oldVersionName = versionName;
				oldProfileName = createProfileName(profileName,
						versionNameX - 25);
			}
		}

		private String createProfileName(String profileName, int maxWidth) {
			String result = profileName;
			if (profileFontMetrics.stringWidth(result) > maxWidth) {
				int widthSum = 0;
				for (int i = 0; i < result.length(); i++) {
					widthSum += profileFontMetrics.charWidth(result.charAt(i));
					if (widthSum > maxWidth) {
						return result.substring(0, i) + "...";
					}
				}
			}
			return result;
		}

		private void drawVersionName(Graphics2D g2d) {
			g2d.setColor(Color.black);
			g2d.setFont(VERSION_NAME_FONT);
			g2d.drawString(oldVersionName, versionNameX, 20);
		}

		private void drawProfileName(Graphics2D g2d) {
			g2d.setColor(Color.black);
			g2d.setFont(PROFILE_NAME_FONT);
			g2d.drawString(oldProfileName, 5, 30);
		}

		private void drawStatus(Graphics2D g2d) {
			g2d.setColor(Color.gray);
			g2d.setFont(STATUS_FONT);
			String statusString = getLoadingStatus();
			int stringWidth = g2d.getFontMetrics().stringWidth(statusString);
			g2d.drawString(statusString, getWidth() - 40 - stringWidth, 32);
		}

		private void drawIcon(Graphics2D g2d) {
			BufferedImage icon = getIcon();
			g2d.drawImage(icon, getWidth() - icon.getWidth() - 5, 4, null);
		}

		private BufferedImage getIcon() {
			if (isLoading) {
				return LOADING_ICON;
			} else if (isReadyToLoad()) {
				return ACTIVE_ICON;
			} else {
				return INACTIVE_ICON;
			}
		}
	}

	private static final Font STATUS_FONT = new Font("arial", Font.BOLD, 10);
	private static final Font VERSION_NAME_FONT = new Font("arial", Font.BOLD,
			16);
	private static final Font PROFILE_NAME_FONT = new Font("arial", Font.BOLD,
			30);
	private static final BufferedImage ACTIVE_ICON = ResourceLoader
			.getImage("active_profile.png");
	private static final BufferedImage INACTIVE_ICON = ResourceLoader
			.getImage("inactive_profile.png");
	private static final BufferedImage LOADING_ICON = ResourceLoader
			.getImage("loading_profile.png");
	private static final Color SELECTED_BG_COLOR = new Color(160, 190, 255);
	private static final Color LOADING_BG_COLOR = new Color(112, 203, 91);
	private static final Color DEFAULT_BG_COLOR = Color.white;

	private Component component;
	private boolean isLoading = false;
	private boolean isSelected = false;

	/**
	 * This cannot be put in the constructor, because that would cause a call to
	 * e.g. getProfileName by the drawing function before the derived class is
	 * constructed.
	 */
	protected void initComponent() {
		this.component = new Component();
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		if (this.isSelected != isSelected) {
			this.isSelected = isSelected;
			component.repaint();
		}
	}

	public Component getComponent() {
		return component;
	}

	public void load() {
		isLoading = true;
		component.repaint();
		doLoad();
	}

	protected void repaintComponent() {
		component.repaint();
	}

	protected abstract void doLoad();

	protected abstract String getLoadingStatus();

	public abstract boolean isReadyToLoad();

	public abstract String getProfileName();

	public abstract String getVersionName();
}
