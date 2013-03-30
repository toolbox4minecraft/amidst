package MoF;
import java.awt.image.BufferedImage;

public class MapObject {
	public String type;
	public int x, y, rx, ry;
	public BufferedImage marker;
	public double localScale = 1.0;
	public boolean selectable = true;
	public double tempDist = 0;
	public MapObject(String eType, int eX, int eY) {
		type = eType;
		x = eX;
		y = eY;
		if (eType == "Stronghold") {
			marker = MapMarker.stronghold;
		} else if (eType == "Village") {
			marker = MapMarker.village;
		} else if (eType == "Slime") {
			marker = MapMarker.slime;
		} else if (eType == "Player") {
			marker = MapMarker.player;
		} else if (eType == "Netherhold") {
			marker = MapMarker.nether;
		} else if (eType == "Temple") {
			marker = MapMarker.pyramid;
		} else if (eType == "Witch") {
			marker = MapMarker.witch;
		}
	}
	public String getName() {
		return type;
	}
	public int getWidth() {
		if (MoF.mainWindow.layerIconMenu.isSelected())
			return (int)(marker.getWidth()*localScale);
		return 0;
	}
	public int getHeight() {
		if (MoF.mainWindow.layerIconMenu.isSelected())
			return (int)(marker.getHeight()*localScale);
		return 0;
	}
	
	public boolean isSelectable() {
		return MoF.mainWindow.layerIconMenu.isSelected();
	}
}
