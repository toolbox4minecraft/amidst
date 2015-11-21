package amidst.minecraft.world.finder;

import java.util.List;

import amidst.map.Fragment;
import amidst.minecraft.world.CoordinatesInWorld;
import amidst.minecraft.world.World;

public abstract class CachedWorldObjectProducer implements WorldObjectProducer {
	protected final World world;
	private List<WorldObject> cache;

	public CachedWorldObjectProducer(World world) {
		this.world = world;
	}

	@Override
	public void produce(CoordinatesInWorld corner, WorldObjectConsumer consumer) {
		initCache();
		produceFindings(corner, consumer);
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
		}
	}

	private void produceFindings(CoordinatesInWorld corner,
			WorldObjectConsumer consumer) {
		if (cache != null) {
			for (WorldObject finding : cache) {
				if (finding.getCoordinates()
						.isInBoundsOf(corner, Fragment.SIZE)) {
					consumer.consume(finding.getCoordinates(),
							finding.getName(), finding.getImage());
				}
			}
		}
	}

	public void resetCache() {
		cache = null;
	}

	protected abstract List<WorldObject> createCache();
}
