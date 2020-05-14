package amidst.gui.profileselect;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

import amidst.ResourceLoader;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;

@NotThreadSafe
public abstract class ProfileComponent implements Comparable<ProfileComponent>{
	@SuppressWarnings("serial")
	private class Component extends JComponent {
		private final FontMetrics statusFontMetrics = getFontMetrics(STATUS_FONT);
		private final FontMetrics versionFontMetrics = getFontMetrics(VERSION_NAME_FONT);
		private final FontMetrics profileFontMetrics = getFontMetrics(PROFILE_NAME_FONT);

		private int versionNameX;
		private int oldWidth;
		private String oldVersionName;
		private String oldProfileName;

		@CalledOnlyBy(AmidstThread.EDT)
		public Component() {
			this.setMinimumSize(new Dimension(300, 40));
			this.setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));
		}

		@CalledOnlyBy(AmidstThread.EDT)
		@Override
		public void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			drawBackground(g2d);
			updateIfNecessary();
			drawProfileIcon(g2d);
			drawVersionName(g2d);
			drawProfileName(g2d);
			drawStatus(g2d);
			drawStatusIcon(g2d);
		}

		@CalledOnlyBy(AmidstThread.EDT)
		private void drawBackground(Graphics2D g2d) {
			g2d.setColor(getBackgroundColor());
			g2d.fillRect(0, 0, getWidth(), getHeight());
		}

		@CalledOnlyBy(AmidstThread.EDT)
		private void updateIfNecessary() {
			int width = getWidth();
			String versionName = getVersionName();
			String profileName = getProfileName();
			if (oldVersionName == null || oldWidth != width || !oldVersionName.equals(versionName)) {
				versionNameX = width - 40 - versionFontMetrics.stringWidth(versionName);
				oldWidth = width;
				oldVersionName = versionName;
				oldProfileName = createProfileName(profileName, versionNameX - 25, 40);
			}
		}

		@CalledOnlyBy(AmidstThread.EDT)
		private String createProfileName(String profileName, int maxWidth, int xOffset) {
			String result = profileName;
			if (profileFontMetrics.stringWidth(result) + xOffset > maxWidth) {
				int widthSum = 0;
				for (int i = 0; i < result.length(); i++) {
					widthSum += profileFontMetrics.charWidth(result.charAt(i));
					if (widthSum + xOffset > maxWidth) {
						return result.substring(0, i) + "...";
					}
				}
			}
			return result;
		}
		
		@CalledOnlyBy(AmidstThread.EDT)
		private void drawProfileIcon(Graphics2D g2d) {
			Image icon = getScaledProfileIcon();
			if(icon != null) {
				g2d.drawImage(icon, 4, 4, null);
			}
		}

		@CalledOnlyBy(AmidstThread.EDT)
		private void drawVersionName(Graphics2D g2d) {
			g2d.setColor(Color.black);
			g2d.setFont(VERSION_NAME_FONT);
			g2d.drawString(oldVersionName, versionNameX, 20);
		}

		@CalledOnlyBy(AmidstThread.EDT)
		private void drawProfileName(Graphics2D g2d) {
			g2d.setColor(Color.black);
			g2d.setFont(PROFILE_NAME_FONT);
			g2d.drawString(oldProfileName, 40, 30);
		}

		@CalledOnlyBy(AmidstThread.EDT)
		private void drawStatus(Graphics2D g2d) {
			g2d.setColor(Color.gray);
			g2d.setFont(STATUS_FONT);
			String loadingStatus = getLoadingStatus();
			int stringWidth = statusFontMetrics.stringWidth(loadingStatus);
			g2d.drawString(loadingStatus, getWidth() - 40 - stringWidth, 32);
		}

		@CalledOnlyBy(AmidstThread.EDT)
		private void drawStatusIcon(Graphics2D g2d) {
			Image icon = getStatusIcon();
			g2d.drawImage(icon, getWidth() - icon.getWidth(null) - 5, 4, null);
		}
	}

	private static final Font STATUS_FONT = new Font("arial", Font.BOLD, 10);
	private static final Font VERSION_NAME_FONT = new Font("arial", Font.BOLD, 16);
	private static final Font PROFILE_NAME_FONT = new Font("arial", Font.BOLD, 30);
	private static final BufferedImage ACTIVE_ICON = ResourceLoader.getImage("/amidst/gui/profileselect/active.png");
	private static final BufferedImage INACTIVE_ICON = ResourceLoader
			.getImage("/amidst/gui/profileselect/inactive.png");
	private static final BufferedImage LOADING_ICON = ResourceLoader.getImage("/amidst/gui/profileselect/loading.png");
	private static final Color SELECTED_BG_COLOR = new Color(160, 190, 255);
	private static final Color LOADING_BG_COLOR = new Color(112, 203, 91);
	private static final Color DEFAULT_BG_COLOR = Color.white;
	private static final Color FAILED_BG_COLOR = new Color(250, 160, 160);
	private static final Color SELECTED_FAILED_BG_COLOR = new Color(205, 175, 208);

	public static final int PREFERRED_WIDTH = 500;
	public static final int PREFERRED_HEIGHT = 40;

	private Component component;
	private boolean isSelected = false;

	/**
	 * This cannot be put in the constructor, because that would cause a call to
	 * e.g. getProfileName by the drawing function before the derived class is
	 * constructed.
	 */
	@CalledOnlyBy(AmidstThread.EDT)
	protected void initComponent() {
		this.component = new Component();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public boolean isSelected() {
		return isSelected;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void setSelected(boolean isSelected) {
		if (this.isSelected != isSelected) {
			this.isSelected = isSelected;
			component.repaint();
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public Component getComponent() {
		return component;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	protected void repaintComponent() {
		component.repaint();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private String getLoadingStatus() {
		if (failedLoading()) {
			return "failed loading";
		} else if (isLoading()) {
			return "loading";
		} else if (failedResolving()) {
			return "not found";
		} else if (isResolving()) {
			return "resolving";
		} else {
			return "found";
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private Image getStatusIcon() {
		if (failedLoading()) {
			return INACTIVE_ICON;
		} else if (isLoading()) {
			return LOADING_ICON;
		} else if (failedResolving()) {
			return INACTIVE_ICON;
		} else if (isResolving()) {
			return INACTIVE_ICON;
		} else {
			return ACTIVE_ICON;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	protected Image getScaledProfileIcon() {
		return null;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private Color getBackgroundColor() {
		if (failedLoading()) {
			if(isSelected) {
				return SELECTED_FAILED_BG_COLOR;
			} else {
				return FAILED_BG_COLOR;
			}
		} else if (isLoading()) {
			return LOADING_BG_COLOR;
		} else if (failedResolving()) {
			if(isSelected) {
				return SELECTED_FAILED_BG_COLOR;
			} else {
				return FAILED_BG_COLOR;
			}
		} else if (isSelected) {
			return SELECTED_BG_COLOR;
		} else {
			return DEFAULT_BG_COLOR;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	protected abstract void load();

	@CalledOnlyBy(AmidstThread.EDT)
	protected abstract boolean isResolving();

	@CalledOnlyBy(AmidstThread.EDT)
	protected abstract boolean failedResolving();

	@CalledOnlyBy(AmidstThread.EDT)
	protected abstract boolean isLoading();

	@CalledOnlyBy(AmidstThread.EDT)
	protected abstract boolean failedLoading();

	@CalledOnlyBy(AmidstThread.EDT)
	protected abstract boolean isReadyToLoad();

	@CalledOnlyBy(AmidstThread.EDT)
	protected abstract String getProfileName();

	@CalledOnlyBy(AmidstThread.EDT)
	protected abstract String getVersionName();

	@CalledOnlyBy(AmidstThread.EDT)
	public abstract void resolveLater();
	
	@Override
	public int compareTo(ProfileComponent o) {
		return this.getProfileName().compareToIgnoreCase(o.getProfileName());
	}
}
