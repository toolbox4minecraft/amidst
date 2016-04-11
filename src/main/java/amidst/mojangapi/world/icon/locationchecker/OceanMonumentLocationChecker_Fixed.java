package amidst.mojangapi.world.icon.locationchecker;

import java.util.List;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

/**
 * Bug https://bugs.mojang.com/browse/MC-65214 was fixed in 15w46a, and the fix
 * changes where Ocean Monuments can appear. This class implements the
 * LocationChecker for after the fix was implemented.
 * 
 * The fix is described here:
 * https://bugs.mojang.com/browse/MC-65214?focusedCommentId
 * =228462&page=com.atlassian
 * .jira.plugin.system.issuetabpanels:comment-tabpanel#comment-228462 That
 * description is quoted below:
 * 
 * ---- "The issue lies in that the server is calculating DEEP_BIOME based on
 * the 1.8 World Generator and NOT the worlds ACTUAL biome from previous
 * versions.
 * 
 * I had a report of a biome in OCEAN (Not Deep), and when I generated a 1.8
 * world with exact same seed, sure enough that location was DEEP_OCEAN in that
 * seed.
 * 
 * The Monument Structure check uses 2 different Biome lookup methods, and the
 * one that does DEEP_BIOME check uses the World Gen calculations, and then the
 * followup check for "Surrounding biomes" uses the actual worlds data.
 * 
 * I temp fixed for my server with the following change: - if
 * (this.c.getWorldChunkManager().getBiome(new BlockPosition(k * 16 + 8, 64, l *
 * 16 + 8), (BiomeBase) null) != BiomeBase.DEEP_OCEAN) { + if
 * (!this.c.getWorldChunkManager().a(k * 16 + 8, l * 16 + 8, 16,
 * Arrays.asList(BiomeBase.DEEP_OCEAN))) {
 * 
 * This issue has more flaws than this report states as it also causes monuments
 * to spawn in the middle of Rivers, Frozen Rivers and Frozen Oceans, which is
 * quite odd to stroll through the mountains and find a monument." ----
 * 
 * Examples: In seed -3189899870270394863, the monuments at (808, 1224) and
 * (-856, 184) are eliminated by this fix.
 * 
 */
@ThreadSafe
public class OceanMonumentLocationChecker_Fixed extends AllValidLocationChecker {
	private static final long MAGIC_NUMBER_FOR_SEED_1 = 341873128712L;
	private static final long MAGIC_NUMBER_FOR_SEED_2 = 132897987541L;
	private static final long MAGIC_NUMBER_FOR_SEED_3 = 10387313L;
	private static final byte MAX_DISTANCE_BETWEEN_SCATTERED_FEATURES = 32;
	private static final byte MIN_DISTANCE_BETWEEN_SCATTERED_FEATURES = 5;
	private static final boolean USE_TWO_VALUES_FOR_UPDATE = true;
	private static final int STRUCTURE_SIZE = 29;
	private static final int STRUCTURE_CENTER_SIZE = 16;

	public OceanMonumentLocationChecker_Fixed(
			long seed,
			BiomeDataOracle biomeDataOracle,
			List<Biome> validBiomesAtMiddleOfChunk,
			List<Biome> validBiomesForStructure) {
		// @formatter:off
		super(new StructureAlgorithm(
						seed,
						MAGIC_NUMBER_FOR_SEED_1,
						MAGIC_NUMBER_FOR_SEED_2,
						MAGIC_NUMBER_FOR_SEED_3,
						MAX_DISTANCE_BETWEEN_SCATTERED_FEATURES,
						MIN_DISTANCE_BETWEEN_SCATTERED_FEATURES,
						USE_TWO_VALUES_FOR_UPDATE
				), new StructureBiomeLocationChecker(
						biomeDataOracle,
						STRUCTURE_CENTER_SIZE,
						validBiomesAtMiddleOfChunk
				), new StructureBiomeLocationChecker(
						biomeDataOracle,
						STRUCTURE_SIZE,
						validBiomesForStructure
				)
		);
		// @formatter:on
	}
}
