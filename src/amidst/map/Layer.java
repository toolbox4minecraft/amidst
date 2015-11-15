package amidst.map;

public abstract class Layer {
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

	public void reload() {
	}

	public void update(float time) {
	}
}
