package amidst.minecraft.world.finder;

import java.awt.image.BufferedImage;
import java.util.List;

import amidst.map.Fragment;
import amidst.minecraft.world.CoordinatesInWorld;
import amidst.minecraft.world.World;

public abstract class CachedFinder implements Finder {
	protected static class Finding {
		private final CoordinatesInWorld coordinates;
		private final String name;
		private final BufferedImage image;

		public Finding(CoordinatesInWorld coordinates, String name,
				BufferedImage image) {
			this.coordinates = coordinates;
			this.name = name;
			this.image = image;
		}
	}

	protected final World world;
	private List<Finding> cache;

	public CachedFinder(World world) {
		this.world = world;
	}

	@Override
	public void find(CoordinatesInWorld corner, FindingConsumer consumer) {
		if (cache == null) {
			cache = createCache();
		}
		produceFindings(corner, consumer);
	}

	private void produceFindings(CoordinatesInWorld corner,
			FindingConsumer consumer) {
		if (cache != null) {
			for (Finding finding : cache) {
				if (finding.coordinates.isInBoundsOf(corner, Fragment.SIZE)) {
					consumer.consume(finding.coordinates, finding.name,
							finding.image);
				}
			}
		}
	}

	public void resetCache() {
		cache = null;
	}

	protected abstract List<Finding> createCache();
}
