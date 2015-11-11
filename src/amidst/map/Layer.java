package amidst.map;

public class Layer {
	private Map map;

	public void setMap(Map map) {
		this.map = map;
	}

	public Map getMap() {
		return map;
	}

	public boolean isVisible() {
		return true;
	}

	public double getMapZoom() {
		return map.getZoom();
	}

	public void reload() {
	}

	public void update(float time) {
	}
}
