package amidst.mojangapi.world.filter;

import amidst.mojangapi.world.World;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.icon.producer.WorldIconCollector;

class StructureFilter extends BaseFilter {

  public StructureFilter(String name, long worldFilterSize) {
    super(name, worldFilterSize);

    //TODO: make configurable
  }

  @Override
protected boolean isValid(World world, short[][] region) {
    WorldIconCollector villageCollector = new WorldIconCollector();

    for (long x = 0; x < worldFilterSize; x += 512) {
      for (long y = 0; y < worldFilterSize; y += 512) {
        CoordinatesInWorld subCorner = CoordinatesInWorld.from(x, y).add(corner);
        world.getVillageProducer().produce(subCorner, villageCollector, null);
      }
    }

    return villageCollector.get().size() > 2;
  }
}