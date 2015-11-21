package amidst.map.layer;

import java.awt.image.BufferedImage;

import amidst.Options;
import amidst.map.Fragment;
import amidst.map.object.MapObject;
import amidst.minecraft.world.CoordinatesInWorld;
import amidst.minecraft.world.finder.FindingConsumer;

public class OceanMonumentLayer extends IconLayer {
	@Override
	public boolean isVisible() {
		return Options.instance.showOceanMonuments.get();
	}

	@Override
	public void generateMapObjects(Fragment fragment) {
		Options.instance.world.getOceanMonuments(fragment.getCorner(),
				createFindingConsumer(fragment));
	}

	private FindingConsumer createFindingConsumer(final Fragment fragment) {
		return new FindingConsumer() {
			@Override
			public void consume(CoordinatesInWorld coordinates, String name,
					BufferedImage image) {
				fragment.addObject(MapObject.from(coordinates, name, image,
						Options.instance.showOceanMonuments));
			}
		};
	}
}
