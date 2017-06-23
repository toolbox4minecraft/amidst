package amidst.filter.json;

import java.lang.reflect.Type;
import java.util.stream.Collectors;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class CriterionDeserializer implements JsonDeserializer<CriterionJson> {
	
	private JsonElement manageShorthands(JsonElement json) {
		if(json.isJsonArray()) { //And criterion shorthand
			JsonObject obj = new JsonObject();
			obj.add("and", json);
			return obj;
			
		}
		
		if(json.isJsonPrimitive()) {//Group reference shorthand
			JsonObject obj = new JsonObject();
			obj.add("group", json);
			return obj;
		}
		
		return json;
	}

	@Override
	public CriterionJson deserialize(JsonElement json, Type type, JsonDeserializationContext context)
			throws JsonParseException {
		json = manageShorthands(json);
		
		if(!json.isJsonObject())
			throw new JsonParseException("invalid element type: " + getTypeString(json));
		
		JsonObject obj = json.getAsJsonObject();
		Class<? extends CriterionJson> realClass = getRealClass(obj);
		if(realClass == null) {
			String summary = obj.entrySet().stream()
								.map(e -> e.getKey())
								.collect(Collectors.joining(", ", "{ ", " }"));
								
			throw new JsonParseException("unknown criterion type: " + summary);
		}
		
		return context.deserialize(json, realClass);
	}
		
	public Class<? extends CriterionJson> getRealClass(JsonObject obj) {
		if(obj.has("group"))
			return CriterionJsonGroup.class;
		
		if(obj.has("and"))
			return CriterionJsonAnd.class;
		
		if(obj.has("or"))
			return CriterionJsonOr.class;
		
		if(obj.has("structures"))
			return CriterionJsonBase.class;
		
		if(obj.has("biomes"))
			return CriterionJsonBase.class;
		
		return null;
	}
	
	private static String getTypeString(JsonElement e) {
		if(e.isJsonArray())
			return "array";
		
		if(e.isJsonObject())
			return "object";
		
		if(e.isJsonPrimitive())
			return "primitive";
	
		if(e.isJsonNull())
			return "null";
		
		return "unknown";
	}
	
}