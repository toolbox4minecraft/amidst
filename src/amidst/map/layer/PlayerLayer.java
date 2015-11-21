package amidst.map.layer;

import amidst.Options;
import amidst.map.Fragment;

public class PlayerLayer extends IconLayer {
	@Override
	public boolean isVisible() {
		return Options.instance.showPlayers.get();
	}

	@Override
	public void generateMapObjects(Fragment fragment) {
		Options.instance.world.getPlayers(fragment.getCorner(),
				createWorldObjectConsumer(fragment));
	}
}
