package amidst.mojangapi.world.filter;

import java.util.List;

import amidst.documentation.Immutable;
import amidst.mojangapi.world.World;

@Immutable
public class WorldFilter_MatchAll extends WorldFilter {
	private final List<WorldFilter> filters;

	public WorldFilter_MatchAll(long worldFilterSize, List<WorldFilter> filters) {
		super(worldFilterSize);
		this.filters = filters;
	}

	@Override
	public boolean isValid(World world) {
		for (WorldFilter filter : filters) {
			if (!filter.isValid(world)) {
				return false;
			}
		}
		return true;
	}
}
