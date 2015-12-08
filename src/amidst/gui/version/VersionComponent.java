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

public abstract class VersionComponent {
	@SuppressWarnings("serial")
	private class Component extends JComponent {
		private String shortenedVersionName;
		private String oldVersionName;
		private int oldWidth = 0;

		public Component() {
			this.shortenedVersionName = getVersionName();
			this.oldVersionName = getVersionName();
			this.setMinimumSize(new Dimension(300, 40));
			this.setPreferredSize(new Dimension(500, 40));
		}

		@Override
		public void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			drawBackground(g2d);
			int versionNameX = drawFullVersionName(g2d);
			drawVersionName(g2d, versionNameX);
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

		private int drawFullVersionName(Graphics2D g2d) {
			g2d.setColor(Color.black);
			g2d.setFont(VERSION_FONT);
			String fullVersionName = getFullVersionName();
			int versionNameX = getWidth() - 40
					- g2d.getFontMetrics().stringWidth(fullVersionName);
			g2d.drawString(fullVersionName, versionNameX, 20);
			return versionNameX;
		}

		private void drawVersionName(Graphics2D g2d, int versionNameX) {
			g2d.setColor(Color.black);
			g2d.setFont(NAME_FONT);
			updateShortenedVersionName(g2d, versionNameX);
			g2d.drawString(shortenedVersionName, 5, 30);
		}

		private void updateShortenedVersionName(Graphics2D g2d, int versionNameX) {
			String versionName = getVersionName();
			if (oldWidth != getWidth() || !oldVersionName.equals(versionName)) {
				shortenedVersionName = createShortenedVersionName(g2d,
						versionName, versionNameX);
				oldWidth = getWidth();
			}
		}

		private String createShortenedVersionName(Graphics2D g2d,
				String versionName, int versionNameX) {
			FontMetrics fontMetrics = g2d.getFontMetrics();
			String result = versionName;
			if (fontMetrics.stringWidth(result) > versionNameX - 25) {
				int widthSum = 0;
				for (int i = 0; i < result.length(); i++) {
					widthSum += fontMetrics.charWidth(result.charAt(i));
					if (widthSum > versionNameX - 25) {
						result = result.substring(0, i) + "...";
						break;
					}
				}
			}
			return result;
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

	private static final Font NAME_FONT = new Font("arial", Font.BOLD, 30);
	private static final Font STATUS_FONT = new Font("arial", Font.BOLD, 10);
	private static final Font VERSION_FONT = new Font("arial", Font.BOLD, 16);
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

	public String getFullVersionName() {
		return getVersionPrefix() + ":" + getVersionName();
	}

	protected void initComponent() {
		this.component = new Component();
	}

	protected void repaintComponent() {
		component.repaint();
	}

	protected abstract void doLoad();

	protected abstract String getLoadingStatus();

	public abstract boolean isReadyToLoad();

	public abstract String getVersionName();

	public abstract String getVersionPrefix();
}
