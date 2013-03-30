package MoF;

import java.awt.image.BufferedImage;

public class Player extends MapObject {
	private String name;
	public boolean needSave;
	public Player(String name, int x, int y) {
		super("Player", x , y);
		needSave = false;
		this.setName(name);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getWidth() {
		return (int)(marker.getWidth()*localScale*3);
	}
	public int getHeight() {
		return (int)(marker.getHeight()*localScale*3);
	}
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
		needSave = true;
	}

	public void setMarker(BufferedImage img) {
		this.marker = img;
	}
}
