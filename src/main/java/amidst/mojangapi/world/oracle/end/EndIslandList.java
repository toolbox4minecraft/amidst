package amidst.mojangapi.world.oracle.end;

import java.util.List;

import amidst.documentation.Immutable;

@Immutable
public class EndIslandList {
	private final List<LargeEndIsland> largeIslands;
	private final List<SmallEndIsland> smallIslands;
	
	public EndIslandList(List<SmallEndIsland> smallIslands, List<LargeEndIsland> largeIslands) {
		this.largeIslands = largeIslands;
		this.smallIslands = smallIslands;
	}
	
	public List<LargeEndIsland> getLargeIslands() {
		return largeIslands;
	}

	public List<SmallEndIsland> getSmallIslands() {
		return smallIslands;
	}
	
}
