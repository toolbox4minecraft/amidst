package amidst.mojangapi.world.icon.locationchecker;

import java.util.List;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

@ThreadSafe
public class VillageAlgorithm implements LocationChecker {
	private final BiomeDataOracle biomeDataOracle;
	private final List<Biome> validBiomes;

	public VillageAlgorithm(BiomeDataOracle biomeDataOracle, List<Biome> validBiomes) {
		this.biomeDataOracle = biomeDataOracle;
		this.validBiomes = validBiomes;
	}

	@Override
	public boolean isValidLocation(int chunkX, int chunkY) {
		// @formatter:off
		/**
		 * Villages start will a well, size 6 x 6, extending to the right and down from
		 * the village spawn coordinates. The well starts the village bounding box at 6 x 6.
		 *
		 * My hypothesis was as follows:
		 *   "If a river or non-valid biome cuts through this area then none of the paths extending from
		 *   the well will generate (as I think they each retest the bounding box against valid biomes as they
		 *   expand the bounding box). If none of the paths generate then I think the Village fails as a certain number
		 *   of buildings are required - it even looks like more than 2 roads may be required."
		 *
		 * Village at [-5008, -8000] and [-6352, -8144] on seed -1364077613 disprove the hypothesis, yet
		 * strangely it still appears to be a fantastic improvement to village accuracy - eliminating 18% of
		 * all villages from the 20km test area with those two being the only false negative I've found (having
		 * checked over half of the eliminated villages). So I'll keep this code in AmidstExporter, but leave
		 * it up to other maintainers to decide if it goes in their versions.
		 *
		 * The village paths must be only biome-testing their own bounding box, rather than the village's, so
		 * beats me why this works so well. I guess because paths each test for bad biomes by an extra 4 blocks
		 * beyond their own bounds, which means that any biome touching the well will most likely be knocking
		 * out 3 of the 4 possible paths, and a village doesn't have much chance of becoming large enough to be
		 * viable from just one path?
		 */
		// @formatter:on

		// For some reason MapGenVillage.Start.Start() adds only 2 to the
		// multiplied coord
		int wellSize = 6;
		int x1 = chunkX * 16 + 2;
		int y1 = chunkY * 16 + 2;
		int x2 = x1 + wellSize - 1;
		int y2 = y1 + wellSize - 1;
		int wellX = (x1 + x2) / 2;
		int wellY = (y1 + y2) / 2;

		// @formatter:off
		/**
		 * There's an arbitraryConstant of 4 in the Minecraft source that's added to bounding
		 * box sizes to get the "structureSize" for areBiomesViable() (e.g. see func_176069_e()), but
		 * adding 4 to this well heuristic eliminates several villages I have confirmed to exist. The
		 * source is hard to read and (as above) I've obviously missed a lot of how the villages work,
		 * so trying a different tact...
		 *
		 * Setting arbitraryConstant to 1 means a wellStructureSize of 3, which means the area
		 * passed to isValidBiome() will exactly match the visual footprint of the well. Setting
		 * arbitraryConstant to 2 will mean a wellStructureSize of 4 which corresponds to a 1-block
		 * lip around the well also being checked for bad biomes (this test is at 1/4th the block
		 * resolution, too).
		 * Testing all villages within 20km of the seed -1364077613, I see that the difference between
		 * arbitraryConstant of 1 vs 2 is that a value of 2 correctly eliminates a further 8 villages
		 * and none of those 8 villages exist in the game, however values above 2 will eliminate more
		 * villages that do exist (e.g. [2672, -5424] and [3376, -3504]).
		 *
		 * So in lieu of a larger data set, and at the risk of optimizing it to the "training" data,
		 * lets set arbitraryConstant to 2 ;)
		 */
		// @formatter:on

		int arbitraryConstant = 2;
		int wellStructureSize = (x2 - x1) / 2 + arbitraryConstant;

		// @formatter:off
		/**
		 * Checking that the well is able to build eliminates most of the false positives, however
		 * a few remain. Here are some villages unable to build sufficiently beyond the
		 * well (i.e. I have confirmed they do not exist in the game), which are not eliminated by
		 * canSpawnWellAtCoords. Found during testing on seed -1364077613:
		 *  *  2912,  2080
		 *  * -5888,  4784
		 *  * -7952, -9424
		 *  *  -272, -7872
		 */
		// @formatter:on

		return biomeDataOracle.isValidBiomeForStructure(wellX, wellY, wellStructureSize, validBiomes);
	}

	@Override
	public boolean hasValidLocations() {
		return !validBiomes.isEmpty();
	}
}
