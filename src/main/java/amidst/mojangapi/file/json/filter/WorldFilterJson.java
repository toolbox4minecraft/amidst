package amidst.mojangapi.file.json.filter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.filter.WorldFilter;

@Immutable
public class WorldFilterJson {
	
	//@formatter: off
	private static final Gson GSON = new GsonBuilder()
					.registerTypeAdapter(CriterionJson.class, new CriterionDeserializer())
					.registerTypeAdapter(CriterionJson.ClusterInfo.class,
										new CriterionJson.ClusterInfoDeserializer())
					.create();
	//@formatter: on
	
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
		return GSON.fromJson(json, WorldFilterJson.class);
	}
	
}