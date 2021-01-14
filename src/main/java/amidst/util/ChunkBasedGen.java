package amidst.util;

import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;

import java.util.stream.IntStream;

public enum ChunkBasedGen {
    ;

    /**
     * Gets the data for a given area by getting the relevant chunks from a ChunkAccessor.
     * <p>
     * Use this to implement WorldAccessors which only support getting data from chunk positions,
     * rather than block positions.
     */
    public static int[] mapChunkBased(int x, int y, int width, int height, boolean useQuarterResolution, ChunkAccessor chunkAccessor) throws MinecraftInterfaceException {
        int shift = useQuarterResolution ? 4 : 1;
        int blockXBegin = x * shift;
        int blockXEnd = (x + width) * shift;
        int blockYBegin = y * shift;
        int blockYEnd = (y + height) * shift;


        int chunkXBegin = (int) Math.floor(blockXBegin / 16.0);
        int chunkXEnd = (int) Math.floor(blockXEnd / 16.0);
        int chunkZBegin = (int) Math.floor(blockYBegin / 16.0);
        int chunkZEnd = (int) Math.floor(blockYEnd / 16.0);

        int[][][] biomesByChunk = new int[chunkZEnd - chunkZBegin + 1][][];
        for (int chunkZ = chunkZBegin; chunkZ <= chunkZEnd; ++chunkZ) {
            biomesByChunk[chunkZ - chunkZBegin] = new int[chunkXEnd - chunkXBegin + 1][];
            for (int chunkX = chunkXBegin; chunkX <= chunkXEnd; ++chunkX) {
                int[] biomes = chunkAccessor.getChunk(chunkZ, chunkX);
                // add to result array
                biomesByChunk[chunkZ - chunkZBegin][chunkX - chunkXBegin] = biomes;
            }
        }
        int[] result = new int[width * height];
        for (int idxY = 0; idxY < height; ++idxY)  {
            for (int idxX = 0; idxX < width; ++idxX) {
                int blockX = (x + idxX) * shift;
                int chunkX = (int) Math.floor(blockX / 16.0);
                int blockY = (y + idxY) * shift;
                int chunkY = (int) Math.floor(blockY / 16.0);
                result[idxX + idxY * width] = biomesByChunk[chunkY - chunkZBegin][chunkX - chunkXBegin][
                        (blockX - chunkX * 16) + (blockY - chunkY * 16) * 16];
            }
        }
        return result;
    }

    public static int getIndex(int blockX, int blockY, int blockZ) {
        return blockY + blockX * 128 + blockZ * 16 * 128;
    }

    public static IntStream streamY63() {
        return IntStream.range(0, 16)
                .flatMap(blockZ -> IntStream.range(0, 16)
                        .map(blockX -> getIndex(blockZ, 63, blockX)));
    }

    @FunctionalInterface
    public interface ChunkAccessor {
        int[] getChunk(int z, int x) throws MinecraftInterfaceException;
    }
}
