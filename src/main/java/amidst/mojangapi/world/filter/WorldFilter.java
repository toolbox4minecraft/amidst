package amidst.mojangapi.world.filter;

import java.util.List;

import amidst.mojangapi.world.World;

public class WorldFilter extends BaseFilter {
	private final List<BaseFilter> filterList;

	public WorldFilter(long worldFilterSize, List<BaseFilter> filters) {
		super(worldFilterSize);

		filterList = filters;
	}

	@Override
	protected boolean isValid(World world, short[][] region) {
		if (filterList.size() == 0) {
			return true;
		}

		for (BaseFilter filter : filterList) {
			if (!filter.isValid(world)) {
				return false;
			}
		}
		return true;
	}
	
	public boolean hasFilters() {
		return filterList.size() > 0;
	}
}
