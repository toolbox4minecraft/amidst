package MoF;

import amidst.map.MapMarkers;

import java.awt.image.BufferedImage;

public class Player extends MapObject {
	private String name;
	public boolean needSave;
	private BufferedImage marker;
	
	public Player(String name, int x, int y) {
		super(MapMarkers.PLAYER, x , y);
		marker = type.image;
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
	
	@Override
	public BufferedImage getImage() {
		return marker;
	}
	
	public void setMarker(BufferedImage img) {
		this.marker = img;
	}
	
	@Override
	public String toString() {
		return "Player \"" + name + "\" at (" + x + ", " + y + ")";
	}
}
