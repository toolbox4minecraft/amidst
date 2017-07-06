package amidst.mojangapi.world.testworld.storage.json;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import amidst.documentation.GsonObject;
import amidst.documentation.Immutable;
import amidst.mojangapi.mocking.FragmentWalker;
import amidst.mojangapi.world.coordinates.Coordinates;
import amidst.mojangapi.world.oracle.EndIsland;
import amidst.mojangapi.world.oracle.EndIslandOracle;

@Immutable
@GsonObject
public class EndIslandsJson {
	public static EndIslandsJson extract(EndIslandOracle oracle, int fragmentsAroundOrigin) {
		SortedMap<Coordinates, List<EndIsland>> result = new TreeMap<>();
		FragmentWalker.walkFragmentsAroundOrigin(fragmentsAroundOrigin).walkCorners(
				corner -> result.put(corner, oracle.getAt(corner)));
		return new EndIslandsJson(result);
	}

	private volatile SortedMap<Coordinates, List<EndIsland>> endIslands;

	public EndIslandsJson() {
	}

	public EndIslandsJson(SortedMap<Coordinates, List<EndIsland>> endIslands) {
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
