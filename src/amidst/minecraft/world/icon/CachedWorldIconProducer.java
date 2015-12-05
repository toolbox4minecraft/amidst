package amidst.minecraft.world.icon;

import java.util.Collections;
import java.util.List;

import amidst.fragment.Fragment;
import amidst.minecraft.world.CoordinatesInWorld;
import amidst.minecraft.world.World;

public abstract class CachedWorldIconProducer extends WorldIconProducer {
	protected final World world;
	private List<WorldIcon> cache;

	public CachedWorldIconProducer(World world) {
		this.world = world;
	}

	@Override
	public void produce(CoordinatesInWorld corner, WorldIconConsumer consumer) {
		initCache();
		produceWorldIcons(corner, consumer);
	}

	public List<WorldIcon> getWorldIcons() {
		initCache();
		return cache;
	}

	public WorldIcon getFirstWorldIcon() {
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

	private void produceWorldIcons(CoordinatesInWorld corner,
			WorldIconConsumer consumer) {
		if (cache != null) {
			for (WorldIcon icon : cache) {
				if (icon.getCoordinates().isInBoundsOf(corner, Fragment.SIZE)) {
					consumer.consume(icon);
				}
			}
		}
	}

	public void resetCache() {
		cache = null;
	}

	protected abstract List<WorldIcon> createCache();
}
