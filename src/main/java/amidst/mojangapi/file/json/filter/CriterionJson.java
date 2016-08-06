package amidst.mojangapi.file.json.filter;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import amidst.documentation.GsonConstructor;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;

public abstract class CriterionJson {
	
	public CoordinatesInWorld center = CoordinatesInWorld.from(0, 0);
	public boolean negate = false;
	public int score = 1;
	
	@GsonConstructor
	public CriterionJson() {}
	
	public static class Base extends CriterionJson {
		public long radius;
		public String shape = null;
		public List<String> biomes;
		public boolean variants = false;
	
		public List<String> structures = null;
	}
	
	public static class ClusterInfo {
		public long radius = 0;
		public int size;
	}
	
	public static class And extends CriterionJson {
		public List<CriterionJson> and;
		
		@GsonConstructor
		public And() {}
	}
	
	public static class Or extends CriterionJson {
		public List<CriterionJson> or;
		int minScore = Integer.MIN_VALUE;
		
		@GsonConstructor
		public Or() {}
	}
	
	public static class Group extends CriterionJson {
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
