package amidst.gui.version;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import amidst.resources.ResourceLoader;

public abstract class VersionComponent extends JComponent {
	protected static Font nameFont = new Font("arial", Font.BOLD, 30);
	protected static Font statusFont = new Font("arial", Font.BOLD, 10);
	protected static Font versionFont = new Font("arial", Font.BOLD, 16);
	protected static BufferedImage activeIcon   = ResourceLoader.getImage("active_profile.png");
	protected static BufferedImage inactiveIcon = ResourceLoader.getImage("inactive_profile.png");
	protected static BufferedImage loadingIcon  = ResourceLoader.getImage("loading_profile.png");
	protected static Color selectedBgColor = new Color(160, 190, 255);
	protected static Color loadingBgColor = new Color(112, 203, 91);
	
	protected boolean selected = false;
	protected boolean isLoading = false;
	
	public VersionComponent() {
		this.setMinimumSize(new Dimension(300, 40));
		this.setPreferredSize(new Dimension(500, 40));
	}
	
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean value) {
		selected = value;
	}
	
	public abstract void load();
	public abstract boolean isReadyToLoad();
	public abstract String getVersionName();
}
