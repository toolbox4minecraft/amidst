package amidst.map.layer;

import amidst.Options;
import amidst.map.Fragment;
import amidst.map.MapMarkers;
import amidst.map.finder.FindingConsumer;
import amidst.map.finder.VillageFinder;
import amidst.map.object.MapObject;
import amidst.minecraft.world.CoordinatesInWorld;

public class VillageLayer extends IconLayer {
	private VillageFinder finder = new VillageFinder();

	@Override
	public boolean isVisible() {
		return Options.instance.showVillages.get();
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
						Options.instance.showVillages));
			}
		};
	}
}
