package amidst.mojangapi.world.filter;

import java.lang.IllegalArgumentException;

import amidst.mojangapi.world.World;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Resolution;

class BiomeFilter extends BaseFilter {
  private static int[] biomes;

  public BiomeFilter(String name, long worldFilterSize) {
    super(name, worldFilterSize);
    //TODO: make configurable
    int[] biomes = {
      Biome.mesa.getIndex(),
      Biome.mesaPlateauF.getIndex(),
      Biome.mesaPlateau.getIndex(),
      Biome.mesaBryce.getIndex(),
      Biome.mesaPlateauFM.getIndex(),
      Biome.mesaPlateauM.getIndex(),
    };
    this.biomes = biomes;
  }

  protected boolean isValid(World world, short[][] region) {
    for (short[] row: region){
      for (short entry: row) {
        if (isValidBiome(entry)) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean isValidBiome(short biomeIndex) {
    for (int test: biomes) {
      if (test == biomeIndex) {
        return true;
      }
    }
    return false;
  }
}