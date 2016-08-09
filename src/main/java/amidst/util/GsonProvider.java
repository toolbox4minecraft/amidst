package amidst.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import amidst.mojangapi.file.json.filter.CriterionDeserializer;
import amidst.mojangapi.file.json.filter.CriterionJson;

public enum GsonProvider {
	;
	
	// @formatter:off
	private static final GsonBuilder BUILDER_LENIENT = new GsonBuilder()
			.enableComplexMapKeySerialization()
			.registerTypeAdapter(
					CriterionJson.class,
					new CriterionDeserializer())
			.registerTypeAdapter(
					CriterionJson.ClusterInfo.class,
					new CriterionJson.ClusterInfoDeserializer());
	// @formatter:on
	
	private static final GsonBuilder BUILDER_STRICT = null; //TODO
	
	private static final Gson LENIENT = BUILDER_LENIENT.create();
	private static final Gson STRICT = BUILDER_STRICT.create();
	
	public static Gson get() {
		return LENIENT;
	}
	
	public static Gson getStrict() {
		return STRICT;
	}
	
	public static GsonBuilder builder() {
		return BUILDER_LENIENT;
	}
	
	public static GsonBuilder builderStrict() {
		return BUILDER_STRICT;
	}
}
