package amidst.minecraft.world.finder;

import java.awt.image.BufferedImage;

import amidst.minecraft.world.CoordinatesInWorld;

public interface FindingConsumer {
	void consume(CoordinatesInWorld coordinates, String name,
			BufferedImage image);
}
