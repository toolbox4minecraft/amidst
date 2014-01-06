package amidst.map;

public class Layer {
	protected Map map;
	
	public void update(float time) {
	
	}

	
	public void setMap(Map map) {
		this.map = map;
	}
	public Map getMap() {
		return map;
	}
	public boolean isVisible() {
		return true;
	}

	public void reload() {
		
	}
}
