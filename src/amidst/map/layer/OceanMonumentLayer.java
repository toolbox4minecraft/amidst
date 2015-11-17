package amidst.map.layer;

import amidst.Options;
import amidst.map.Fragment;
import amidst.map.finder.OceanMonumentFinder;

public class OceanMonumentLayer extends IconLayer {
	private OceanMonumentFinder finder = new OceanMonumentFinder();

	@Override
	public boolean isVisible() {
		return Options.instance.showOceanMonuments.get();
	}

	@Override
	public void generateMapObjects(Fragment fragment) {
		finder.generateMapObjects(Options.instance.world,
				Options.instance.showOceanMonuments, fragment);
	}
}
