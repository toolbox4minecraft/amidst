package amidst.mojangapi.file.json.filter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import amidst.documentation.GsonConstructor;
import amidst.documentation.JsonField;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.filter.WorldFilter;
import amidst.util.GsonProvider;

public class WorldFilterJson {

	
	@JsonField(optional=true)
	public Defaults defaults = new Defaults();
	
	@JsonField(optional=true)
	public Map<String, CriterionJson> groups = Collections.emptyMap();
	
	@JsonField(optional=true)
	public CriterionJson match = null;
	
	@JsonField(optional=true)
	public List<String> ignore = Collections.emptyList();
	
	public static class Defaults {
		@JsonField(optional=true)
		public CoordinatesInWorld center = null;
		
		@JsonField(optional=true)
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
		return GsonProvider.getStrict().fromJson(json, WorldFilterJson.class);
	}
	
}