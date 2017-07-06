package amidst.mojangapi.world.testworld.storage.json;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;

import amidst.documentation.GsonObject;
import amidst.documentation.Immutable;
import amidst.logging.AmidstLogger;
import amidst.logging.AmidstMessageBox;
import amidst.mojangapi.mocking.FragmentWalker;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.coordinates.Coordinates;
import amidst.mojangapi.world.coordinates.Region;
import amidst.mojangapi.world.icon.WorldIcon;
import amidst.mojangapi.world.icon.producer.NameFilteredWorldIconCollector;
import amidst.mojangapi.world.icon.producer.WorldIconProducer;

@Immutable
@GsonObject
public class CoordinatesCollectionJson {
	public static CoordinatesCollectionJson extractWorldSpawn(World world) {
		Coordinates spawn = world.getSpawnWorldIcon().getCoordinates();
		return new CoordinatesCollectionJson(createSortedSet(spawn));
	}

	public static CoordinatesCollectionJson extractStrongholds(World world) {
		List<WorldIcon> strongholds = world.getStrongholdWorldIcons();
		return new CoordinatesCollectionJson(createSortedSet(strongholds));
	}

	public static <T> CoordinatesCollectionJson extractWorldIcons(
			WorldIconProducer<T> producer,
			String name,
			Function<Region.Box, T> additionalDataFactory,
			int fragmentsAroundOrigin,
			int minimalNumberOfCoordinates) {
		NameFilteredWorldIconCollector consumer = new NameFilteredWorldIconCollector(name);
		FragmentWalker.walkFragmentsAroundOrigin(fragmentsAroundOrigin).walk(
				region -> producer.produce(region, consumer, additionalDataFactory.apply(region)));
		SortedSet<Coordinates> coordinates = createSortedSet(consumer.get());
		if (coordinates.size() < minimalNumberOfCoordinates) {
			String message = "not enough coordinates for '" + name + "'";
			AmidstLogger.error(message);
			AmidstMessageBox.displayError("Error", message);
		}
		return new CoordinatesCollectionJson(coordinates);
	}

	private static SortedSet<Coordinates> createSortedSet(List<WorldIcon> icons) {
		SortedSet<Coordinates> result = new TreeSet<>();
		for (WorldIcon icon : icons) {
			result.add(icon.getCoordinates());
		}
		return result;
	}

	private static SortedSet<Coordinates> createSortedSet(Coordinates coordinates) {
		SortedSet<Coordinates> result = new TreeSet<>();
		result.add(coordinates);
		return result;
	}

	private volatile SortedSet<Coordinates> coordinates;

	public CoordinatesCollectionJson() {
	}

	public CoordinatesCollectionJson(SortedSet<Coordinates> coordinates) {
		this.coordinates = coordinates;
	}

	public boolean contains(Coordinates coordinates) {
		for (Coordinates coordinatesArrayEntry : this.coordinates) {
			if (coordinates.equals(coordinatesArrayEntry)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((coordinates == null) ? 0 : coordinates.hashCode());
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
		if (!(obj instanceof CoordinatesCollectionJson)) {
			return false;
		}
		CoordinatesCollectionJson other = (CoordinatesCollectionJson) obj;
		if (coordinates == null) {
			if (other.coordinates != null) {
				return false;
			}
		} else if (!coordinates.equals(other.coordinates)) {
			return false;
		}
		return true;
	}
}
