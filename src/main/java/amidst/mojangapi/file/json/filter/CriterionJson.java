package amidst.mojangapi.file.json.filter;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import amidst.documentation.GsonConstructor;
import amidst.documentation.JsonField;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;

public abstract class CriterionJson {
	
	@JsonField(optional=true)
	public CoordinatesInWorld center = CoordinatesInWorld.from(0, 0);
	
	@JsonField(optional=true)
	public boolean negate = false;
	
	@JsonField(optional=true)
	public int score = 0;
	
	@GsonConstructor
	public CriterionJson() {}
	
	public static class Base extends CriterionJson {
		@JsonField()
		public long radius;
		
		@JsonField(optional=true)
		public String shape = null;
		
		@JsonField(optional=true)
		public List<String> biomes = null;
		
		@JsonField(optional=true)
		public boolean variants = false;
	
		@JsonField(optional=true)
		public List<String> structures = null;
		
		@JsonField(optional=true, require={"structures"})
		public ClusterInfo cluster;
	}
	
	public static class ClusterInfo {
		@JsonField(optional=true)
		public long radius = 0;
		
		@JsonField()
		public int size;
	}
	
	public static class And extends CriterionJson {
		@JsonField()
		public List<CriterionJson> and;
		
		@GsonConstructor
		public And() {}
	}
	
	public static class Or extends CriterionJson {
		@JsonField()
		public List<CriterionJson> or;
		
		@JsonField(optional=true)
		int minScore = Integer.MIN_VALUE;
		
		@GsonConstructor
		public Or() {}
	}
	
	public static class Group extends CriterionJson {
		@JsonField()
		public String group;
		
		@GsonConstructor
		public Group() {}
	}
	
	
	public static class ClusterInfoDeserializer implements JsonDeserializer<ClusterInfo> {
		@Override
		public ClusterInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			if(json.isJsonPrimitive()) {
				JsonObject obj = new JsonObject();
				obj.add("size", json);
			}
			
			return context.deserialize(json, ClusterInfo.class);
		}
		
	}
}
