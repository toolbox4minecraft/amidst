package amidst.map;

import java.awt.image.BufferedImage;

public class MapObjectPlayer extends MapObject {
	public String name;
	public boolean needSave;
	private BufferedImage marker;
	
	public MapObjectPlayer(String name, int x, int y) {
		super(MapMarkers.PLAYER, x , y);
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
	
	@Override
	public String toString() {
		return "MapObjectPlayer \"" + name + "\" at (" + x + ", " + y + ")";
	}
}
