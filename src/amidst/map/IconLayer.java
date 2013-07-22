package amidst.map;

import MoF.ChunkManager;
import amidst.preferences.BooleanPrefModel;


public class IconLayer {
	public String name;
	protected Map map;
	
	protected ChunkManager chunkManager;
	
	private BooleanPrefModel visible = null;
	
	public IconLayer(String name) {
		this.name = name;
	}
	
	public void setMap(Map map) {
		this.map = map;
	}
	public boolean isVisible() {
		return (visible == null) || visible.get();
	}
	public void setVisibilityPref(BooleanPrefModel visibility) {
		visible = visibility;
	}
	
	public void setChunkManager(ChunkManager chunkManager) {
		this.chunkManager = chunkManager;
	}
	
	public void generateMapObjects(Fragment frag) {
		
	}
}
