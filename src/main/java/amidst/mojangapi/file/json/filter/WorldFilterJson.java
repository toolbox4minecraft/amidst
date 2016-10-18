package amidst.mojangapi.file.json.filter;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.JsonParseException;

import amidst.documentation.GsonConstructor;
import amidst.documentation.JsonField;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.filter.Criterion;
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
	
	
	public WorldFilter validate() throws WorldFilterParseException {
		CriterionJsonContext ctx = new CriterionJsonContext(defaults, groups::get);
		
		CriterionJsonContext ctx2 = ctx.withName("<ignore>");
		for(String gname: ignore) {
			if(!groups.containsKey(gname))
				ctx2.error("the group " + gname + " doesn't exist");
		}
		
		Set<String> ignoreSet = new HashSet<>(ignore);
		
		List<Criterion> criteria = groups.keySet().stream()
			.filter(name -> !ignoreSet.contains(name))
			.map(name -> ctx.convertCriterion(name).orElse(null))
			.collect(Collectors.toList());
		
		Criterion main = match.validate(ctx.withName("<main>")).orElse(null);
		
		if(!ctx.getErrors().isEmpty())
			throw new WorldFilterParseException(ctx.getErrors());
		
		return new WorldFilter(defaults.center, criteria, main);
	}

	public static WorldFilterJson fromJSON(String json) throws WorldFilterParseException {
		try {
			return GsonProvider.getStrict().fromJson(json, WorldFilterJson.class);
		} catch (JsonParseException e) {
			throw new WorldFilterParseException(e);
		}
	}
	
	
}