package amidst.minecraft.world.object;

import java.util.Collections;
import java.util.List;

import amidst.map.Fragment;
import amidst.minecraft.world.CoordinatesInWorld;
import amidst.minecraft.world.World;

public abstract class CachedWorldObjectProducer extends WorldObjectProducer {
	protected final World world;
	private List<WorldObject> cache;

	public CachedWorldObjectProducer(World world) {
		this.world = world;
	}

	@Override
	public void produce(CoordinatesInWorld corner, WorldObjectConsumer consumer) {
		initCache();
		produceWorldObjects(corner, consumer);
	}

	public List<WorldObject> getWorldObjects() {
		initCache();
		return cache;
	}

	public WorldObject getFirstWorldObject() {
		initCache();
		if (cache.isEmpty()) {
			return null;
		} else {
			return cache.get(0);
		}
	}

	private void initCache() {
		if (cache == null) {
			cache = createCache();
			if (cache == null) {
				cache = Collections.emptyList();
			}
		}
	}

	private void produceWorldObjects(CoordinatesInWorld corner,
			WorldObjectConsumer consumer) {
		if (cache != null) {
			for (WorldObject worldObject : cache) {
				if (worldObject.getCoordinates().isInBoundsOf(corner,
						Fragment.SIZE)) {
					consumer.consume(worldObject);
				}
			}
		}
	}

	public void resetCache() {
		cache = null;
	}

	protected abstract List<WorldObject> createCache();
}
