package amidst.mojangapi.world.filter;

import java.lang.IllegalArgumentException;
import java.util.ArrayList;
import java.util.List;

import amidst.mojangapi.world.World;

public class WorldFilter extends BaseFilter {
  private List<BaseFilter> filterList;
  
  public WorldFilter(String name, long worldFilterSize) {
    super(name, worldFilterSize);

    //TODO: make the sub filters configurable    
    filterList = new ArrayList();
    //filterList.add(new BiomeFilter("mesa", worldFilterSize));
    filterList.add(new StructureFilter("village", worldFilterSize));
  }

  protected boolean isValid(World world, short[][] region) {
    for (BaseFilter filter: filterList) {
      if (!filter.isValid(world, region)) {
        return false;
      }
    }
    return true;
  }
}
