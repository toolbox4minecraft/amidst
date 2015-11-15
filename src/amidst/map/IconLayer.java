package amidst.map;

public abstract class IconLayer extends Layer {
	public abstract void generateMapObjects(Fragment fragment);

	public void clearMapObjects(Fragment fragment) {
		fragment.setObjectsLength(0);
	}
}
