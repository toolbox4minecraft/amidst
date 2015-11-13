package amidst.gui.version;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import amidst.Application;
import amidst.minecraft.remote.RemoteMinecraft;
import amidst.resources.ResourceLoader;
import amidst.version.MinecraftProfile;

public abstract class VersionComponent {
	@SuppressWarnings("serial")
	private class Component extends JComponent {
		private String shortenedDisplayName;
		private String oldDisplayName;
		private int oldWidth = 0;

		public Component(String displayName) {
			this.shortenedDisplayName = displayName;
			this.oldDisplayName = displayName;
			this.setMinimumSize(new Dimension(300, 40));
			this.setPreferredSize(new Dimension(500, 40));
		}

		@Override
		public void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			drawBackground(g2d);
			int versionNameX = drawVersionName(g2d);
			drawDisplayName(g2d, versionNameX);
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

		private int drawVersionName(Graphics2D g2d) {
			FontMetrics fontMetrics;
			g2d.setColor(Color.black);
			g2d.setFont(VERSION_FONT);
			fontMetrics = g2d.getFontMetrics();
			int versionNameX = getWidth() - 40
					- fontMetrics.stringWidth(getVersionName());
			g2d.drawString(getVersionName(), versionNameX, 20);
			return versionNameX;
		}

		private void drawDisplayName(Graphics2D g2d, int versionNameX) {
			g2d.setColor(Color.black);
			g2d.setFont(NAME_FONT);
			updateShortenedDisplayName(g2d, versionNameX);
			g2d.drawString(shortenedDisplayName, 5, 30);
		}

		private void updateShortenedDisplayName(Graphics2D g2d, int versionNameX) {
			if (oldWidth != getWidth() || oldDisplayName != getDisplayName()) {
				shortenedDisplayName = createShortenedDisplayName(g2d,
						versionNameX);
				oldWidth = getWidth();
			}
		}

		private String createShortenedDisplayName(Graphics2D g2d,
				int versionNameX) {
			FontMetrics fontMetrics = g2d.getFontMetrics();
			String result = getDisplayName();
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
	private Application application;
	private boolean isLoading = false;
	private boolean isSelected = false;

	public VersionComponent(Application application) {
		this.application = application;
		this.component = new Component(getDisplayName());
	}

	protected void versionSelected(RemoteMinecraft minecraftInterface) {
		application.displayMapWindow(minecraftInterface);
	}

	protected void versionSelected(MinecraftProfile profile) {
		application.displayMapWindow(profile);
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean value) {
		isSelected = value;
	}

	public Component getComponent() {
		return component;
	}

	protected void repaintComponent() {
		component.repaint();
	}

	public void load() {
		isLoading = true;
		component.repaint();
		new Thread(new Runnable() {
			@Override
			public void run() {
				doLoad();
			}
		}).start();
	}

	protected abstract void doLoad();

	protected abstract String getLoadingStatus();

	public abstract boolean isReadyToLoad();

	public abstract String getVersionName();

	public abstract String getDisplayName();
}
