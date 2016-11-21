package amidst.fragment.colorprovider;

import java.awt.image.BufferedImage;
import java.util.List;

import amidst.ResourceLoader;
import amidst.documentation.ThreadSafe;
import amidst.fragment.Fragment;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.oracle.EndIsland;

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
				(int) (xAsQuarter << 2),
				(int) (yAsQuarter << 2),
				xAsQuarter >> 2,
				yAsQuarter >> 2,
				(int) (x % TEXTURES_WIDTH),
				(int) (y % TEXTURES_HEIGHT),
				fragment.getEndIslands());
	}

	private int getColorAt(
			int x,
			int y,
			long chunkX,
			long chunkY,
			int textureX,
			int textureY,
			List<EndIsland> endIslands) {
		// Determine whether this
		float maxInfluence = getMaxInfluence(x, y, endIslands);
		if (maxInfluence >= INFLUENCE_FADE_START) {
			// Draw endstone island
			return getEndStoneTextureAt(textureX, textureY);
		} else {
			return getFadingColorAt(chunkX, chunkY, textureX, textureY, maxInfluence);
		}
	}

	private float getMaxInfluence(int x, int y, List<EndIsland> endIslands) {
		float result = -100.0f;
		for (EndIsland island : endIslands) {
			float influence = island.influenceAtBlock(x, y);
			if (result < influence) {
				result = influence;
			}
		}
		return result;
	}

	private int getFadingColorAt(long chunkX, long chunkY, int textureX, int textureY, float maxInfluence) {
		int result = VOID_TRANSPARENT_BLACK;
		if (showRockyShores(chunkX, chunkY)) {
			result = getRockyShoresTextureAt(textureX, textureY);
		}
		if (maxInfluence > INFLUENCE_FADE_FINISH) {
			// Fade out the endstone - this is the edge of an island
			int pixelAlpha = result >>> 24;
			int fadingIslandAlpha = getFadingIslandAlpha(maxInfluence);
			if (fadingIslandAlpha > pixelAlpha) {
				// favor the island pixel instead of the rocky shores pixel
				// (Should look perfect without needing to blend, because
				// rocky shore is still endstone texture)
				return getFadedEndStoneTextureAt(textureX, textureY, fadingIslandAlpha);
			}
		}
		return result;
	}

	/**
	 * Determine if the chunk may contain miniature islands.
	 */
	private boolean showRockyShores(long chunkX, long chunkY) {
		return (chunkX * chunkX + chunkY * chunkY) > 4096;
	}

	private int getFadingIslandAlpha(float maxInfluence) {
		return 255 - (int) (255 * (INFLUENCE_FADE_START - maxInfluence) / INFLUENCE_FADE_RANGE);
	}

	private int getEndStoneTextureAt(int textureX, int textureY) {
		return TEXTURES.getRGB(textureX, textureY);
	}

	/**
	 * Unfortunately the "rocky shore" miniature islands are not deterministic
	 * from the world seed, like chorus plants they are decorations whose PRNG
	 * state depends on the order chunks are created/explored in. This makes me
	 * sad :( Let's use a symbolic texture, since we can't plot them properly.
	 */
	private int getRockyShoresTextureAt(int textureX, int textureY) {
		return TEXTURES.getRGB(textureX, textureY + TEXTURES_HEIGHT);
	}

	private int getFadedEndStoneTextureAt(int textureX, int textureY, int alpha) {
		return (getEndStoneTextureAt(textureX, textureY) & 0x00FFFFFF) | (alpha << 24);
	}
}
