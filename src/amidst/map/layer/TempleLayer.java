package amidst.map.layer;

import java.awt.image.BufferedImage;

import amidst.Options;
import amidst.map.Fragment;
import amidst.map.object.MapObject;
import amidst.minecraft.world.CoordinatesInWorld;
import amidst.minecraft.world.finder.WorldObjectConsumer;

public class TempleLayer extends IconLayer {
	@Override
	public boolean isVisible() {
		return Options.instance.showTemples.get();
	}

	@Override
	public void generateMapObjects(Fragment fragment) {
		Options.instance.world.getTemples(fragment.getCorner(),
				createFindingConsumer(fragment));
	}

	private WorldObjectConsumer createFindingConsumer(final Fragment fragment) {
		return new WorldObjectConsumer() {
			@Override
			public void consume(CoordinatesInWorld coordinates, String name,
					BufferedImage image) {
				fragment.addObject(MapObject.from(coordinates, name, image,
						Options.instance.showTemples));
			}
		};
	}
}
