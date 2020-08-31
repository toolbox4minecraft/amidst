package amidst.mojangapi.world.icon.producer;

import java.util.List;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.mojangapi.world.icon.locationchecker.AllValidLocationChecker;
import amidst.mojangapi.world.icon.locationchecker.StructureBiomeLocationChecker;
import amidst.mojangapi.world.icon.type.DefaultWorldIconTypes;
import amidst.mojangapi.world.icon.type.ImmutableWorldIconTypeProvider;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

/**
 * Bug https://bugs.mojang.com/browse/MC-65214 was fixed in 15w46a, and the fix
 * changes where Ocean Monuments can appear. This class implements the
 * RegionalStructureProducer for after the fix was implemented.
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
public class OceanMonumentProducer_Fixed extends RegionalStructureProducer<Void> {
	private static final Resolution RESOLUTION = Resolution.CHUNK;
	private static final int OFFSET_IN_WORLD = 8;
	private static final Dimension DIMENSION = Dimension.OVERWORLD;
	private static final boolean DISPLAY_DIMENSION = false;
	
	private static final long SALT = 10387313L;
	private static final byte SPACING = 32;
	private static final byte SEPARATION = 5;
	private static final boolean IS_TRIANGULAR = true;
	
	private static final int STRUCTURE_SIZE = 29;
	private static final int STRUCTURE_CENTER_SIZE = 16;

	public OceanMonumentProducer_Fixed(
			long worldSeed,
			BiomeDataOracle biomeDataOracle,
			List<Biome> validBiomesAtMiddleOfChunk,
			List<Biome> validBiomesForStructure,
			boolean buggyStructureCoordinateMath) {
		
		super(RESOLUTION,
			  OFFSET_IN_WORLD,
			  new AllValidLocationChecker(
				  new StructureBiomeLocationChecker(biomeDataOracle, STRUCTURE_CENTER_SIZE, validBiomesAtMiddleOfChunk),
				  new StructureBiomeLocationChecker(biomeDataOracle, STRUCTURE_SIZE, validBiomesForStructure)
			  ),
			  new ImmutableWorldIconTypeProvider(DefaultWorldIconTypes.OCEAN_MONUMENT),
			  DIMENSION,
			  DISPLAY_DIMENSION,
			  worldSeed,
			  SALT,
			  SPACING,
			  SEPARATION,
			  IS_TRIANGULAR,
			  buggyStructureCoordinateMath
			);
	}
}
