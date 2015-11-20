package amidst.minecraft.world.finder;

import amidst.map.MapMarkers;
import amidst.minecraft.world.CoordinatesInWorld;

public interface FindingConsumer {
	void consume(CoordinatesInWorld coordinates, MapMarkers mapMarker);
}
