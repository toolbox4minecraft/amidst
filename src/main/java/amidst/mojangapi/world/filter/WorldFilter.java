package amidst.mojangapi.world.filter;

import java.util.List;

import amidst.mojangapi.world.World;

public class WorldFilter extends BaseFilter {
	private final List<BaseFilter> filters;

	public WorldFilter(long worldFilterDistance, List<BaseFilter> filters) {
		super(worldFilterDistance);
		this.filters = filters;
	}

	@Override
	protected boolean isValid(World world, short[][] region) {
		for (BaseFilter filter : filters) {
			if (!filter.isValid(world)) {
				return false;
			}
		}
		return true;
	}
}
