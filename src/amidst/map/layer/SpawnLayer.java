package amidst.map.layer;

import amidst.Options;
import amidst.map.Fragment;

public class SpawnLayer extends IconLayer {
	@Override
	public boolean isVisible() {
		return Options.instance.showSpawn.get();
	}

	@Override
	public void generateMapObjects(Fragment fragment) {
		Options.instance.world.getSpawn(fragment.getCorner(),
				createWorldObjectConsumer(fragment));
	}
}
