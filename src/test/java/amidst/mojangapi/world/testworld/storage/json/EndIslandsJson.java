package amidst.mojangapi.world.testworld.storage.json;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;
import amidst.mojangapi.mocking.FragmentCornerWalker;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.oracle.EndIsland;
import amidst.mojangapi.world.oracle.EndIslandOracle;

@Immutable
public class EndIslandsJson {
	public static EndIslandsJson extract(EndIslandOracle oracle, int fragmentsAroundOrigin) {
		SortedMap<CoordinatesInWorld, List<EndIsland>> result = new TreeMap<>();
		FragmentCornerWalker.walkFragmentsAroundOrigin(fragmentsAroundOrigin).walk(
				corner -> result.put(corner, oracle.getAt(corner)));
		return new EndIslandsJson(result);
	}

	private volatile SortedMap<CoordinatesInWorld, List<EndIsland>> endIslands;

	@GsonConstructor
	public EndIslandsJson() {
	}

	public EndIslandsJson(SortedMap<CoordinatesInWorld, List<EndIsland>> endIslands) {
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
		if (!(obj instanceof EndIslandsJson)) {
			return false;
		}
		EndIslandsJson other = (EndIslandsJson) obj;
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
