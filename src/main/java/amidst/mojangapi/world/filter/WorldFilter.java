package amidst.mojangapi.world.filter;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import amidst.mojangapi.world.SeedHistoryLogger;
import amidst.mojangapi.world.World;

public class WorldFilter extends BaseFilter {
	private final List<BaseFilter> filterList;
	private final String name;
	private SeedHistoryLogger matchLogger;

	/**
	 * The buffer array to pass to individual filters. This will be done at
	 * quarter filter resolution to improve memory and performance with
	 * negligible cost to accuracy
	 */
	private short[][] evaluatedRegion;

	public WorldFilter(long worldFilterSize, List<BaseFilter> filters, String name) {
		super(worldFilterSize);

		this.name = name;
		this.filterList = filters;
	}

	public void setLogger(SeedHistoryLogger logger) {
		this.matchLogger = logger;
	}

	public boolean isValid(World world) {
		if (this.evaluatedRegion == null) {
			// init at quarter size
			this.evaluatedRegion = new short[(int) this.quarterFilterSize][(int) this.quarterFilterSize];
		}

		// fill the data array using quarter resolution
		// this increases performance and reduces memory usage
		// with negligible cost to accuracy
		world.getBiomeDataOracle().populateArray(corner, this.evaluatedRegion, true);

		return isValid(world, this.evaluatedRegion);
	}

	@Override
	protected boolean isValid(World world, short[][] region) {
		if (filterList.size() == 0) {
			return true;
		}
		// create a map of the different groupings
		Map<String, List<BaseFilter>> map = new HashMap<String, List<BaseFilter>>();
		for (BaseFilter filter : filterList) {
			if (!map.containsKey(filter.group)) {
				List<BaseFilter> list = new ArrayList<BaseFilter>();
				list.add(filter);
				map.put(filter.group, list);
			} else {
				map.get(filter.group).add(filter);
			}
		}

		boolean hadMatch = false;
		for (Map.Entry<String, List<BaseFilter>> entry : map.entrySet()) {
			int score = 0;
			boolean isMatch = true;
			for (BaseFilter filter : entry.getValue()) {
				if (filter.isValid(world, evaluatedRegion)) {
					score += filter.scoreValue;
				} else if (filter.scoreValue == 0) {
					isMatch = false;
					// break as zero scores are required
					// and this filter group will fail
					break;
				}
			}
			if (isMatch) {
				matchLogger.log(world.getRecognisedVersion(), world.getWorldSeed(), this.name, entry.getKey(), score);
				hadMatch = true;
			}
		}

		return hadMatch;
	}

	public boolean hasFilters() {
		return filterList.size() > 0;
	}
}
