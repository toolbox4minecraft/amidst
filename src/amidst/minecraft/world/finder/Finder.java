package amidst.minecraft.world.finder;

import amidst.minecraft.world.CoordinatesInWorld;

public interface Finder {
	void find(CoordinatesInWorld corner, FindingConsumer consumer);
}
