package amidst.map;

import java.awt.image.BufferedImage;

public class MapObjectPlayer extends MapObject {
	public String name;
	public boolean needSave;
	private BufferedImage marker;
	public int globalX, globalY;
	public Fragment parentFragment = null;
	
	public MapObjectPlayer(String name, int x, int y) {
		super(MapMarkers.PLAYER,
				((x < 0)?Fragment.SIZE:0) + x % Fragment.SIZE,
				((y < 0)?Fragment.SIZE:0) + y % Fragment.SIZE);
		globalX = x;
		globalY = y;
		marker = type.image;
		needSave = false;
		this.name = name;
	}
	
	
	@Override
	public int getWidth() {
		return (int)(marker.getWidth()*localScale);
	}
	@Override
	public int getHeight() {
		return (int)(marker.getHeight()*localScale);
	}
	public void setPosition(int x, int y) {
		this.globalX = x;
		this.globalY = y;
		this.x = ((x < 0)?Fragment.SIZE:0) + x % Fragment.SIZE;
		this.y = ((y < 0)?Fragment.SIZE:0) + y % Fragment.SIZE;
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
	public String getName() {
		return name;
	}
	@Override
	public String toString() {
		return "Player \"" + name + "\" at (" + x + ", " + y + ")";
	}
}
