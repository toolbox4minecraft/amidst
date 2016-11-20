package amidst.mojangapi.world.icon.locationchecker;

public class EndCityLocationChecker extends AllValidLocationChecker {
	private static final long MAGIC_NUMBER_FOR_SEED_1 = 341873128712L;
	private static final long MAGIC_NUMBER_FOR_SEED_2 = 132897987541L;
	private static final long MAGIC_NUMBER_FOR_SEED_3 = 10387313L;
	private static final byte MAX_DISTANCE_BETWEEN_SCATTERED_FEATURES = 20;
	private static final byte MIN_DISTANCE_BETWEEN_SCATTERED_FEATURES = 11;
	private static final boolean USE_TWO_VALUES_FOR_UPDATE = true;

	public EndCityLocationChecker(long seed) {
		super(
				new StructureAlgorithm(
						seed,
						MAGIC_NUMBER_FOR_SEED_1,
						MAGIC_NUMBER_FOR_SEED_2,
						MAGIC_NUMBER_FOR_SEED_3,
						MAX_DISTANCE_BETWEEN_SCATTERED_FEATURES,
						MIN_DISTANCE_BETWEEN_SCATTERED_FEATURES,
						USE_TWO_VALUES_FOR_UPDATE));
	}
}
