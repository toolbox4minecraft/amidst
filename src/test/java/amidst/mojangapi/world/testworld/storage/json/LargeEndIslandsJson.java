package amidst.mojangapi.world.testworld.storage.json;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;
import amidst.mojangapi.mocking.FragmentCornerWalker;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.oracle.end.EndIslandOracle;
import amidst.mojangapi.world.oracle.end.LargeEndIsland;

@Immutable
public class LargeEndIslandsJson {
	public static LargeEndIslandsJson extract(EndIslandOracle oracle, int fragmentsAroundOrigin) {
		SortedMap<CoordinatesInWorld, List<LargeEndIsland>> result = new TreeMap<>();
		FragmentCornerWalker.walkFragmentsAroundOrigin(fragmentsAroundOrigin).walk(
				corner -> result.put(corner, oracle.getLargeIslandsAt(corner)));
		return new LargeEndIslandsJson(result);
	}

	private volatile SortedMap<CoordinatesInWorld, List<LargeEndIsland>> endIslands;

	@GsonConstructor
	public LargeEndIslandsJson() {
	}

	public LargeEndIslandsJson(SortedMap<CoordinatesInWorld, List<LargeEndIsland>> endIslands) {
		this.endIslands = endIslands;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((endIslands == null) ? 0 : endIslands.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof LargeEndIslandsJson)) {
			return false;
		}
		LargeEndIslandsJson other = (LargeEndIslandsJson) obj;
		if (endIslands == null) {
			if (other.endIslands != null) {
				return false;
			}
		} else if (!endIslands.equals(other.endIslands)) {
			return false;
		}
		return true;
	}
}
