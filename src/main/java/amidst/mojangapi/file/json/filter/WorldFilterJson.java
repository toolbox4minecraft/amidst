package amidst.mojangapi.file.json.filter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import amidst.documentation.GsonConstructor;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.filter.WorldFilter;
import amidst.util.GsonProvider;

public class WorldFilterJson {


	public Defaults defaults = new Defaults();
	public Map<String, CriterionJson> groups = Collections.emptyMap();
	public CriterionJson match = null;
	public List<String> ignore = Collections.emptyList();
	
	public static class Defaults {
		public CoordinatesInWorld center = null;
		public String shape = "circle";
	}
	
	@GsonConstructor
	public WorldFilterJson() {
	}
	
	
	public Optional<WorldFilter> validate() {
		//TODO validation
		return Optional.empty();
	}

	public static WorldFilterJson fromJSON(String json) {
		return GsonProvider.get().fromJson(json, WorldFilterJson.class);
	}
	
}