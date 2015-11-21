package amidst.map.layer;

import amidst.Options;
import amidst.map.Fragment;

public class StrongholdLayer extends IconLayer {
	@Override
	public boolean isVisible() {
		return Options.instance.showStrongholds.get();
	}

	@Override
	public void generateMapObjects(Fragment fragment) {
		Options.instance.world.getNetherFortresses(
				fragment.getCorner(),
				createWorldObjectConsumer(fragment,
						Options.instance.showStrongholds));
	}
}
