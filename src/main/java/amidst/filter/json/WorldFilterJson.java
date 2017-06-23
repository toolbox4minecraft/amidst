package amidst.filter.json;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonParseException;

import amidst.documentation.GsonObject;
import amidst.documentation.JsonField;
import amidst.filter.Criterion;
import amidst.filter.WorldFilter;
import amidst.mojangapi.world.coordinates.Coordinates;
import amidst.parsing.json.GsonProvider;

@GsonObject
public class WorldFilterJson {

	
	@JsonField(optional=true)
	public Defaults defaults = new Defaults();
	
	@JsonField(optional=true)
	public Map<String, CriterionJson> groups = Collections.emptyMap();
	
	@JsonField(optional=true)
	public CriterionJson match = null;
	
	@JsonField(optional=true)
	public List<String> ignore = Collections.emptyList();
	
	@GsonObject
	public static class Defaults {
		@JsonField(optional=true)
		public Coordinates center = null;
		
		@JsonField(optional=true)
		public String shape = "circle";
	}

	public WorldFilterJson() {
	}
	
	
	public WorldFilter validate() throws WorldFilterParseException {
		CriterionJsonContext ctx = new CriterionJsonContext(defaults, groups::get);
		
		Set<String> ignoreSet = makeIgnoreSet(ctx.withName("<ignore>"));
		
		Map<String, Criterion> criteria = new HashMap<>();
	
		for(String name: groups.keySet()) {
			if(!ignoreSet.contains(name))
				criteria.put(name, ctx.convertCriterion(name).orElse(null));
		}
		
		Criterion main = match.validate(ctx.withName("<main>")).orElse(null);
		
		if(!ctx.getErrors().isEmpty())
			throw new WorldFilterParseException(ctx.getErrors());
		
		return new WorldFilter(defaults.center, criteria, main);
	}
	
	private Set<String> makeIgnoreSet(CriterionJsonContext ctx) {
		for(String gname: ignore) {
			if(!groups.containsKey(gname))
				ctx.error("the group " + gname + " doesn't exist");
		}
		
		return new HashSet<>(ignore);
	}

	public static WorldFilterJson fromJSON(String json) throws WorldFilterParseException {
		try {
			return GsonProvider.getStrict().fromJson(json, WorldFilterJson.class);
		} catch (JsonParseException e) {
			throw new WorldFilterParseException(e);
		}
	}
	
	
}