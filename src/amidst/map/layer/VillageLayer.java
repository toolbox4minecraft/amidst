package amidst.map.layer;

import amidst.Options;
import amidst.map.Fragment;
import amidst.map.finder.VillageFinder;

public class VillageLayer extends IconLayer {
	private VillageFinder finder = new VillageFinder();

	@Override
	public boolean isVisible() {
		return Options.instance.showVillages.get();
	}

	@Override
	public void generateMapObjects(Fragment fragment) {
		finder.generateMapObjects(Options.instance.world, this, fragment);
	}
}
