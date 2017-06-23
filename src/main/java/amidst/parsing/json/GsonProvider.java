package amidst.parsing.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
	
	private static GsonBuilder createBuilder(GsonBuilder base) {
		// @formatter:off
		return base
			.enableComplexMapKeySerialization();
		// @formatter:on
	}
	
	public static GsonBuilder builder() {
		return createBuilder(new GsonBuilder());
	}
	
	public static GsonBuilder builderStrict() {
		//We ensure that the strict verification is done AFTER the right subclasses have been detected.
		return createBuilder(new GsonBuilder().registerTypeAdapterFactory(new StrictTypeAdapterFactory()));
	}
}
