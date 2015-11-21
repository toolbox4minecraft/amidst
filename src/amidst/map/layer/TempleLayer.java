package amidst.map.layer;

import amidst.Options;
import amidst.map.Fragment;

public class TempleLayer extends IconLayer {
	@Override
	public boolean isVisible() {
		return Options.instance.showTemples.get();
	}

	@Override
	public void generateMapObjects(Fragment fragment) {
		Options.instance.world.getNetherFortresses(
				fragment.getCorner(),
				createWorldObjectConsumer(fragment,
						Options.instance.showTemples));
	}
}
