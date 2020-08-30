package amidst.mojangapi.world.icon.locationchecker;

import amidst.documentation.Immutable;
import amidst.util.FastRand;

/**
 * Empirical testing suggests this version of the algorithm works all the way
 * back to b1.8, and the Minecraft Wiki says b1.8 was when mineshafts were
 * introduced, but since Amidst versions only go back as far as
 * RecognisedVersion.Vb1_8_1 I won't bother to check for pre-mineshaft versions.
 *
 * I've not decompiled the very early Minecraft versions, and TheMasterCaver
 * points out that "for older versions it is possible that the second part of
 * this code, nextInt(80) < (max of absolute value of chunk coordinates), may
 * not have been present, resulting in mineshafts being equally as common near
 * the origin as they currently are 80 or more chunks away." I've included
 * TheMasterCaver's comment because my empirical testing that Amidst mineshafts
 * do appear in the game can't tell us whether the very early versions have
 * fewer mineshafts near the origin.
 */
@Immutable
public class MineshaftAlgorithm_Original extends MineshaftAlgorithm_Base {
	public MineshaftAlgorithm_Original(long seed) {
		super(seed);
	}

	@Override
	protected boolean getResult(int chunkX, int chunkY, FastRand random) {
		return random.nextInt(100) == 0;
	}
}
