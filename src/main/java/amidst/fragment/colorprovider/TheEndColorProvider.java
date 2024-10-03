package amidst.fragment.colorprovider;

import java.awt.image.BufferedImage;
import java.util.List;

import amidst.ResourceLoader;
import amidst.documentation.ThreadSafe;
import amidst.fragment.Fragment;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.oracle.end.EndIslandList;
import amidst.mojangapi.world.oracle.end.LargeEndIsland;
import amidst.mojangapi.world.oracle.end.SmallEndIsland;

@ThreadSafe
public class TheEndColorProvider implements ColorProvider {
	private static final int VOID_TRANSPARENT_BLACK = 0x00000000;

	private static final BufferedImage TEXTURES = ResourceLoader.getImage("/amidst/gui/main/endtextures.png");

	private static final int TEXTURES_WIDTH = TEXTURES.getWidth();
	private static final int TEXTURES_HEIGHT = TEXTURES.getHeight() >> 1;

	/**
	 * INFLUENCE_FADE_FINISH must be lower than INFLUENCE_FADE_START, so that
	 * islands fades out as influence declines.
	 */
	private static final float INFLUENCE_FADE_START = 0;
	private static final float INFLUENCE_FADE_FINISH = -8;
	private static final float INFLUENCE_FADE_RANGE = INFLUENCE_FADE_START - INFLUENCE_FADE_FINISH;

	@Override
	public int getColorAt(Dimension dimension, Fragment fragment, long cornerX, long cornerY, int x, int y) {
		long xAsQuarter = cornerX + x;
		long yAsQuarter = cornerY + y;
		return getColorAt(
				xAsQuarter << 2,
				yAsQuarter << 2,
				xAsQuarter >> 2,
				yAsQuarter >> 2,
				(int) (x % TEXTURES_WIDTH),
				(int) (y % TEXTURES_HEIGHT),
				fragment.getEndIslands());
	}

	private int getColorAt(
			long x,
			long y,
			long chunkX,
			long chunkY,
			int textureX,
			int textureY,
			EndIslandList endIslands) {
		
		float maxInfluence = getMaxInfluence(x, y, endIslands);
		if (maxInfluence >= INFLUENCE_FADE_START) {
			// Draw endstone island
			return getEndStoneTextureAt(textureX, textureY);
		} else {
			// Draw fade and small islands
			return getOuterColorAt(x, y, chunkX, chunkY, textureX, textureY, maxInfluence, endIslands);
		}
	}

	private float getMaxInfluence(long x, long y, EndIslandList endIslands) {
		float result = -100.0f;
		for (LargeEndIsland island : endIslands.getLargeIslands()) {
			float influence = island.influenceAtBlock(x, y);
			if (result < influence) {
				result = influence;
			}
		}
		return result;
	}

	private int getOuterColorAt(
			long x,
			long y,
			long chunkX,
			long chunkY,
			int textureX,
			int textureY,
			float maxInfluence,
			EndIslandList endIslands) {
		int result = VOID_TRANSPARENT_BLACK;
		
		// The small islands list is null if the version doesn't support them
		if (endIslands.getSmallIslands() != null) {
			// Small islands can leak into other biomes if they spawn close enough, so we want to set this to when large islands start fading so they merge smoothly
			if(maxInfluence <= INFLUENCE_FADE_START) {
				result = getSmallIslandSSAAPixel(x, y, textureX, textureY, endIslands.getSmallIslands());
			}
		} else if (showOldRockyShores(chunkX, chunkY)) {
			result = getRockyShoresTextureAt(textureX, textureY);
		}
		
		if (maxInfluence > INFLUENCE_FADE_FINISH) {
			// Fade out the endstone - this is the edge of an island
			int pixelAlpha = (result >>> 24) + getFadingIslandAlpha(maxInfluence);
			// Add alphas together to blend
			return getFadedEndStoneTextureAt(textureX, textureY, pixelAlpha);
		}
		return result;
	}
	
	private static final double ALPHA_INCREMENT = 63.75d;
	private static final int[] NEIGHBORING_PIXEL_TABLE = {
			0, 0,
			1, 0,
			0, 1,
			1, 1
	};
	
	// Anti-aliased pixel through taking 4 samples and blending them
	private int getSmallIslandSSAAPixel(long x, long y, int textureX, int textureY, List<SmallEndIsland> smallIslands) {
		double alpha = 0;
		for(SmallEndIsland smallIsland : smallIslands) {
			for(int i = 0; i <= 3; i++) {
				if(smallIsland.isOnIsland(x + NEIGHBORING_PIXEL_TABLE[(i * 2)], y + NEIGHBORING_PIXEL_TABLE[(i * 2) + 1])) {
					alpha += ALPHA_INCREMENT;
				}
			}
		}
		
		return alpha == 0 ? VOID_TRANSPARENT_BLACK : getFadedEndStoneTextureAt(textureX, textureY, (int) alpha);
	}

	private int getFadingIslandAlpha(float maxInfluence) {
		return 255 - (int) (255 * (INFLUENCE_FADE_START - maxInfluence) / INFLUENCE_FADE_RANGE);
	}
	
	/**
	 * Determine whether to show the rocky shores texture.
	 */
	private boolean showOldRockyShores(long chunkX, long chunkY) {
		return (chunkX * chunkX + chunkY * chunkY) > 4096L;
	}

	private int getEndStoneTextureAt(int textureX, int textureY) {
		return TEXTURES.getRGB(textureX, textureY);
	}

	/**
	 * Unfortunately the "rocky shore" miniature islands are not deterministic
	 * from the world seed, like chorus plants they are decorations whose PRNG
	 * state depends on the order chunks are created/explored in. This makes me
	 * sad :( Let's use a symbolic texture, since we can't plot them properly.
	 * 
	 * EDIT: This isn't true past 1.13, they can be generated from the seed.
	 */
	private int getRockyShoresTextureAt(int textureX, int textureY) {
		return TEXTURES.getRGB(textureX, textureY + TEXTURES_HEIGHT);
	}

	private int getFadedEndStoneTextureAt(int textureX, int textureY, int alpha) {
		return (getEndStoneTextureAt(textureX, textureY) & 0x00FFFFFF) | (Math.min(alpha, 0xFF) << 24);
	}
}
