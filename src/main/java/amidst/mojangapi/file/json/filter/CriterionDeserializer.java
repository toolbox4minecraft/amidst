package amidst.mojangapi.file.json.filter;

import java.lang.reflect.Type;

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
		
		return context.deserialize(json, getRealClass(json.getAsJsonObject()));
	}
		
	public Class<? extends CriterionJson> getRealClass(JsonObject obj) {
		if(obj.has("group"))
			return CriterionJson.Group.class;
		
		if(obj.has("and"))
			return CriterionJson.And.class;
		
		if(obj.has("or"))
			return CriterionJson.Or.class;
		
		if(obj.has("structures"))
			return CriterionJson.Base.class;
		
		if(obj.has("biomes"))
			return CriterionJson.Base.class;
		
		throw new JsonParseException("unknown criterion type");
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