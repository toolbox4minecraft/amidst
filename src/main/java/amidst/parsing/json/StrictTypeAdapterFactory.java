package amidst.parsing.json;

import java.io.IOException;
import java.lang.reflect.Field;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import amidst.documentation.GsonObject;
import amidst.documentation.JsonField;
import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * This class generates TypeAdapters which verify that the json data contains all required fields
 * (see amidst.documentation.JsonField) and doesn't contains any unknown fields.
 */
public class StrictTypeAdapterFactory implements TypeAdapterFactory {
	
	@SuppressWarnings("unchecked")
	private static List<Class<?>> BASE_JSON_CLASSES = Arrays.asList(new Class<?>[]{
		Map.class,
		List.class,
		String.class
	});

	@Override
	public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
		Class<?> rawType = type.getRawType();
		
		if(rawType.isPrimitive() || rawType.isEnum() || BASE_JSON_CLASSES.contains(rawType))
			return null;
		
		return new StrictTypeAdapter<>(gson.getDelegateAdapter(this, type), rawType);
	}
	
	
	private static class StrictTypeAdapter<T> extends TypeAdapter<T> {
		private static final JsonParser PARSER = new JsonParser();
		
		private TypeAdapter<T> delegate;
		private Class<?> type;
		private Map<String, Field> fields;
		private boolean ignoreUnknown;
		
		public StrictTypeAdapter(TypeAdapter<T> delegate, Class<?> type) {
			GsonObject annotation = type.getAnnotation(GsonObject.class);
			if(annotation == null)
				throw new IllegalArgumentException("the type " + type.getName() + " must have the GsonObject annotation");
			ignoreUnknown = annotation.ignoreUnknown();
			
			this.delegate = delegate;
			this.type = type; 
			this.fields = new HashMap<>();
			
			while(type != Object.class) {
				for(Field f: type.getDeclaredFields())
					this.fields.put(f.getName(), f);
				type = type.getSuperclass();
			}
		}

		@Override
		public void write(JsonWriter out, T value) throws IOException {
			delegate.write(out, value);
		}
		
		@Override
		public T read(JsonReader in) throws IOException {
			if(in.peek() != JsonToken.BEGIN_OBJECT)
				return delegate.read(in);

			JsonObject obj = PARSER.parse(in).getAsJsonObject();
			
			int fieldCount = 0;
			for(Field f: fields.values()) {
				if(verifyField(obj, f, false))
					fieldCount++;
			}
			
			if(!ignoreUnknown && fieldCount < obj.entrySet().size()) {
				for(Entry<String, ?> e: obj.entrySet()) {
					if(!fields.containsKey(e.getKey())) {
						throw new JsonParseException("unknown field " + e.getKey() + " in type " + type.getName());
					}
				}
			}
			
			return delegate.fromJsonTree(obj);
		}
		
		private boolean verifyField(JsonObject obj, Field f, boolean unconditionallyRequire) throws JsonParseException {
			JsonField annotation = f.getAnnotation(JsonField.class);

			if(hasField(obj, f)) {
				if(annotation == null)
					return true;
				
				for(String name: annotation.require()) {
					Field required = fields.get(name);
					if(required == null)
						throw new InvalidParameterException(
							"the required field '" + name + "' doesn't exists in " + type.getName());
					
					verifyField(obj, required, true);
				}
				return true;
				
			} else if(!annotation.optional() || unconditionallyRequire) {
				throw new JsonParseException("field " + f.getName() + " is required for type " + type.getName());
			}
			
			return false;
		}
		
		private boolean hasField(JsonObject obj, Field f) {
			JsonElement attr = obj.get(f.getName());
			
			if(attr != null)
				return attr.isJsonNull() ? false : true;
			
			SerializedName annotation = f.getAnnotation(SerializedName.class);
			if(annotation == null)
				return true;
			
			if(obj.has(annotation.value()))
				return true;
			
			for(String name: annotation.alternate()) {
				if(obj.has(name))
					return true;
			}
			
			return false;
		}
	}

}
