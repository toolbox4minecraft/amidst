package amidst.map;

import java.awt.image.BufferedImage;

import MoF.SaveLoader;

public class MapObjectPlayer extends MapObject {
	public String name;
	public boolean needSave;
	private BufferedImage marker;
	public int globalX, globalY;
	
	public MapObjectPlayer(String name, int x, int y) {
		super(MapMarkers.PLAYER,
				(x < 0)?(Fragment.SIZE + x % Fragment.SIZE):(x % Fragment.SIZE),
				(y < 0)?(Fragment.SIZE + y % Fragment.SIZE):(y % Fragment.SIZE));
		globalX = x;
		globalY = y;
		marker = type.image;
		needSave = false;
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
	public String getName() {
		return name;
	}
	@Override
	public String toString() {
		return "Player \"" + name + "\" at (" + x + ", " + y + ")";
	}
}
