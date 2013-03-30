package MoF;

public class NetherMapObject extends MapObject {

	public NetherMapObject(String eType, int eX, int eY) {
		super(eType, eX, eY);
	}
	public int getWidth() {
		if (MoF.mainWindow.layerNetherMenu.isSelected()) {
			return (int)(marker.getWidth()*localScale);
		}
		return 0;
	}
	public int getHeight() {
		if (MoF.mainWindow.layerNetherMenu.isSelected()) {
			return (int)(marker.getHeight()*localScale);
		} 
		return 0;
	}
	
	public boolean isSelectable() {
		return MoF.mainWindow.layerNetherMenu.isSelected();
	}

}
