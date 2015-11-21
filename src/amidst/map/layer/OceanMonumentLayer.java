package amidst.map.layer;

import amidst.Options;
import amidst.map.Fragment;

public class OceanMonumentLayer extends IconLayer {
	@Override
	public boolean isVisible() {
		return Options.instance.showOceanMonuments.get();
	}

	@Override
	public void generateMapObjects(Fragment fragment) {
		Options.instance.world.getOceanMonuments(fragment.getCorner(),
				createWorldObjectConsumer(fragment));
	}
}
