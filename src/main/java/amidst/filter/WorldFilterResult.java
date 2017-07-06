package amidst.filter;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import amidst.mojangapi.world.World;
import amidst.mojangapi.world.WorldOptions;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.coordinates.Coordinates;
import amidst.mojangapi.world.icon.type.DefaultWorldIconTypes;

public class WorldFilterResult {
	
	public static class ResultItem {
		public Biome biome;
		public final EnumSet<DefaultWorldIconTypes> icons = EnumSet.noneOf(DefaultWorldIconTypes.class);
	}
	
	private Set<String> optionalGoals = new HashSet<>();
	private Map<Coordinates, ResultItem> items = new HashMap<>();
	
	private WorldOptions worldOptions;
	
	public WorldFilterResult(WorldOptions worldOptions) {
		this.worldOptions = worldOptions;
	}
	
	public WorldFilterResult(World world) {
		this(world.getWorldOptions());
	}

	public WorldOptions getWorldOptions() {
		return worldOptions;
	}
	
	public void addOptionalGoal(String name) {
		optionalGoals.add(name);
	}
	
	public Set<String> getOptionalGoals() {
		return Collections.unmodifiableSet(optionalGoals);
	}
	
	public ResultItem getItemFor(Coordinates coordinates) {
		return items.computeIfAbsent(coordinates, c -> new ResultItem());
	}
	
	public Map<Coordinates, ResultItem> getItems() {
		return Collections.unmodifiableMap(items);
	}
	
	@Override
	public String toString() {
		StringBuffer str = new StringBuffer("match for (");
		str.append(worldOptions.getWorldType());
		str.append(", ");
		str.append(worldOptions.getWorldSeed().getLong());
		str.append(") {\n");
		str.append("  goals: ");
		str.append(optionalGoals.stream().map(g -> g.toString()).collect(Collectors.joining(", ")));
		for(Map.Entry<Coordinates, ResultItem> e: items.entrySet()) {
			str.append("\n  at ");
			str.append(e.getKey());
			str.append(": ");
			ResultItem item = e.getValue();
			if(item.biome != null) {
				str.append(item.biome.getName());
				if(!item.icons.isEmpty())
					str.append("; ");
					
			}
			str.append(item.icons.stream().map(i -> i.toString()).collect(Collectors.joining(", ")));
		}
		str.append("\n}");
		
		return str.toString();
	}
	
}
