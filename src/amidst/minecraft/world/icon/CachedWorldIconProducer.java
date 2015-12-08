package amidst.minecraft.world.icon;

import java.util.Collections;
import java.util.List;

import amidst.fragment.Fragment;
import amidst.minecraft.RecognisedVersion;
import amidst.minecraft.world.CoordinatesInWorld;
import amidst.minecraft.world.World;

public abstract class CachedWorldIconProducer extends WorldIconProducer {
	protected final World world;
	protected final RecognisedVersion recognisedVersion;

	private List<WorldIcon> cache;

	public CachedWorldIconProducer(World world,
			RecognisedVersion recognisedVersion) {
		this.world = world;
		this.recognisedVersion = recognisedVersion;
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
			List<WorldIcon> result = createCache();
			if (result == null) {
				cache = Collections.emptyList();
			} else {
				cache = Collections.unmodifiableList(result);
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
