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
		Options.instance.world.getNetherFortresses(
				fragment.getCorner(),
				createWorldObjectConsumer(fragment,
						Options.instance.showOceanMonuments));
	}
}
