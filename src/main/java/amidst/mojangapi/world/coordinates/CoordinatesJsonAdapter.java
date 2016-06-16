package amidst.mojangapi.world.coordinates;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class CoordinatesJsonAdapter extends TypeAdapter<CoordinatesInWorld> {

	@Override
	public CoordinatesInWorld read(JsonReader reader) throws IOException {
		if(reader.peek() == JsonToken.NULL) {
			reader.nextNull();
			return null;
		}
		
		long x = 0;
		long y = 0;
		
		if(reader.peek() == JsonToken.BEGIN_ARRAY) {
			reader.beginArray();
			x = reader.nextLong();
			y = reader.nextLong();
			reader.endArray();
			
		} else {
			int propRead = 0;
			reader.beginObject();
			while(reader.hasNext()) {
				switch(reader.nextName()) {
				case "xInWorld":
					x = reader.nextLong();
					break;
				case "yInWorld":
					y = reader.nextLong();
					break;
				default:
					throw new IOException("invalid name");
				}
				propRead++;
				if(propRead > 2)
					throw new IOException("too many properties");
			}
			reader.endObject();
		}
		
		return CoordinatesInWorld.from(x, y);
	}

	@Override
	public void write(JsonWriter writer, CoordinatesInWorld coo) throws IOException {
		if(coo == null) {
			writer.nullValue();
			return;
		}
		
		// @formatter:off
		writer.beginArray()
			  .value(coo.getX())
			  .value(coo.getY())
			  .endArray();
		// @formatter:on
	}

}