package amidst.mojangapi.world.filter;

import amidst.mojangapi.world.World;
import amidst.mojangapi.world.biome.Biome;

class BiomeFilter extends BaseFilter {
  private int[] biomes;

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

  @Override
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