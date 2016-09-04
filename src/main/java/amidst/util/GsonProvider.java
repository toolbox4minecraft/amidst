package amidst.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import amidst.mojangapi.file.json.filter.CriterionDeserializer;
import amidst.mojangapi.file.json.filter.CriterionJson;
import amidst.mojangapi.file.json.filter.CriterionJsonBase;

public enum GsonProvider {
	;


	private static final Gson LENIENT = builder().create();
	private static final Gson STRICT = builderStrict().create();
	
	public static Gson get() {
		return LENIENT;
	}
	
	public static Gson getStrict() {
		return STRICT;
	}
	
	public static GsonBuilder builder() {
		// @formatter:off
		return new GsonBuilder()
				.enableComplexMapKeySerialization()
				.registerTypeAdapter(
					CriterionJson.class,
					new CriterionDeserializer())
				.registerTypeAdapter(
					CriterionJsonBase.ClusterInfo.class,
					new CriterionJsonBase.ClusterInfoDeserializer());
		// @formatter:on
	}
	
	public static GsonBuilder builderStrict() {
		return builder().registerTypeAdapterFactory(new StrictTypeAdapterFactory());
	}
}
