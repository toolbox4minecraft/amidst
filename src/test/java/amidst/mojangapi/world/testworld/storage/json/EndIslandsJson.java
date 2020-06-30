package amidst.mojangapi.world.testworld.storage.json;

import java.util.SortedMap;
import java.util.TreeMap;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;
import amidst.mojangapi.mocking.FragmentCornerWalker;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.oracle.end.EndIslandList;
import amidst.mojangapi.world.oracle.end.EndIslandOracle;

@Immutable
public class EndIslandsJson {
	public static EndIslandsJson extract(EndIslandOracle oracle, int fragmentsAroundOrigin) {
		SortedMap<CoordinatesInWorld, EndIslandList> result = new TreeMap<>();
		FragmentCornerWalker.walkFragmentsAroundOrigin(fragmentsAroundOrigin).walk(
				corner -> result.put(corner, oracle.getAt(corner)));
		return new EndIslandsJson(result);
	}

	private volatile SortedMap<CoordinatesInWorld, EndIslandList> endIslands;

	@GsonConstructor
	public EndIslandsJson() {
	}

	public EndIslandsJson(SortedMap<CoordinatesInWorld, EndIslandList> endIslands) {
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
