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
		Options.instance.world.getStrongholds(fragment.getCorner(),
				createWorldObjectConsumer(fragment));
	}
}
