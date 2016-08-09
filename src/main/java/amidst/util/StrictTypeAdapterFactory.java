package amidst.util;

import java.io.IOException;
import java.lang.reflect.Field;
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

import amidst.documentation.JsonField;

/**
 * This class generates TypeAdapters which verify that the json data contains all required fields
 * (see amidst.documentation.JsonField) and doesn't contains any unknown fields.
 */
public class StrictTypeAdapterFactory implements TypeAdapterFactory {

	@Override
	public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
		return new StrictTypeAdapter<>(gson.getDelegateAdapter(this, type), type.getRawType());
	}
	
	private static class StrictTypeAdapter<T> extends TypeAdapter<T> {
		TypeAdapter<T> delegate;
		Class<?> type;
		
		public StrictTypeAdapter(TypeAdapter<T> delegate, Class<?> type) {
			this.delegate = delegate;
			this.type = type;
		}

		@Override
		public void write(JsonWriter out, T value) throws IOException {
			delegate.write(out, value);
		}
		
		private static final JsonParser PARSER = new JsonParser();
		
		@Override
		public T read(JsonReader in) throws IOException {
			if(in.peek() != JsonToken.BEGIN_OBJECT)
				return delegate.read(in);

			JsonObject obj = PARSER.parse(in).getAsJsonObject();
			
			int fieldCount = 0;
			for(Field f: type.getFields()) {
				if(verifyField(obj, f, false));
				fieldCount++;
			}
			
			if(fieldCount > type.getFields().length) {
				for(Entry<String, ?> e: obj.entrySet()) {
					if(!hasField(e.getKey()))
						throw new JsonParseException("unknown field " + e.getKey());
				}
			}
			
			return delegate.fromJsonTree(obj);
		}
		
		private boolean verifyField(JsonObject obj, Field f, boolean unconditionallyRequire) throws JsonParseException {
			JsonField annotation = f.getAnnotation(JsonField.class);
			
			if(hasField(obj, f)) {
				try {
					for(String name: annotation.require())
						verifyField(obj, type.getField(name), true);
					
				} catch (NoSuchFieldException | SecurityException e) {
					new RuntimeException("Can't access field :" + e.getMessage());
				}
				return true;
				
			} else if(!annotation.optional() || unconditionallyRequire) {
				throw new JsonParseException("Field " + f.getName() + " is required for type " + type.getSimpleName());
			}
			
			return false;
		}
		
		private boolean hasField(String fieldName) {
			try {
				type.getField(fieldName);
			} catch (NoSuchFieldException | SecurityException e) {
				//The field doesn't exist, or isn't accessible
				return false;
			}
			
			//The field exists
			return true;
		}
		
		private boolean hasField(JsonObject obj, Field f) {
			JsonElement attr = obj.get(f.getName());
			
			if(attr != null)
				return attr.isJsonNull() ? false : true;
			
			SerializedName annotation = f.getAnnotation(SerializedName.class);
			if(annotation == null)
				return false;
			
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
