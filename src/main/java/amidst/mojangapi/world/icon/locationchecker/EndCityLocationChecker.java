package amidst.mojangapi.world.icon.locationchecker;

public class EndCityLocationChecker extends AllValidLocationChecker {
	private static final long STRUCTURE_SALT = 10387313L;
	private static final byte MAX_DISTANCE_BETWEEN_SCATTERED_FEATURES = 20;
	private static final byte MIN_DISTANCE_BETWEEN_SCATTERED_FEATURES = 11;
	private static final boolean USE_TWO_VALUES_FOR_UPDATE = true;

	public EndCityLocationChecker(long seed) {
		super(
				new StructureAlgorithm(
						seed,
						STRUCTURE_SALT,
						MAX_DISTANCE_BETWEEN_SCATTERED_FEATURES,
						MIN_DISTANCE_BETWEEN_SCATTERED_FEATURES,
						USE_TWO_VALUES_FOR_UPDATE));
	}
}
