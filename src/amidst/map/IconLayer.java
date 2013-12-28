package amidst.map;

import amidst.preferences.BooleanPrefModel;


public class IconLayer {
	public String name;
	protected Map map;
	
	private BooleanPrefModel visible = null;
	
	public IconLayer(String name) {
		this.name = name;
	}
	
	public void setMap(Map map) {
		this.map = map;
	}
	public Map getMap() {
		return map;
	}
	public boolean isVisible() {
		return (visible == null) || visible.get();
	}
	public void setVisibilityPref(BooleanPrefModel visibility) {
		visible = visibility;
	}
	
	
	public void generateMapObjects(Fragment frag) {
		
	}
	
	public void clearMapObjects(Fragment frag) {
		frag.objectsLength = 0;
	}
	

	public void reload() {
		
	}
}
