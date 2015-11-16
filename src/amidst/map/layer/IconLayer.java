package amidst.map.layer;

import amidst.map.Fragment;

public abstract class IconLayer extends Layer {
	public abstract void generateMapObjects(Fragment fragment);

	public void clearMapObjects(Fragment fragment) {
		fragment.clearMapObject();
	}
}
