package amidst.map.layer;

import amidst.Options;
import amidst.map.Fragment;
import amidst.map.MapMarkers;
import amidst.map.finder.FindingConsumer;
import amidst.map.finder.OceanMonumentFinder;
import amidst.map.object.MapObject;
import amidst.minecraft.world.CoordinatesInWorld;

public class OceanMonumentLayer extends IconLayer {
	private OceanMonumentFinder finder = new OceanMonumentFinder();

	@Override
	public boolean isVisible() {
		return Options.instance.showOceanMonuments.get();
	}

	@Override
	public void generateMapObjects(Fragment fragment) {
		finder.generateMapObjects(Options.instance.world, fragment.getCorner(),
				createFindingConsumer(fragment));
	}

	private FindingConsumer createFindingConsumer(final Fragment fragment) {
		return new FindingConsumer() {
			@Override
			public void consume(CoordinatesInWorld coordinates,
					MapMarkers mapMarker) {
				fragment.addObject(MapObject.from(coordinates, mapMarker,
						Options.instance.showOceanMonuments));
			}
		};
	}
}
