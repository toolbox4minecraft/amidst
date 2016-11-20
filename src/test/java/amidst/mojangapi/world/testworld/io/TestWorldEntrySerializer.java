package amidst.mojangapi.world.testworld.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import amidst.documentation.Immutable;
import amidst.mojangapi.world.testworld.storage.json.BiomeDataJson;
import amidst.mojangapi.world.testworld.storage.json.CoordinatesCollectionJson;
import amidst.mojangapi.world.testworld.storage.json.EndIslandsJson;
import amidst.mojangapi.world.testworld.storage.json.SlimeChunksJson;
import amidst.mojangapi.world.testworld.storage.json.WorldMetadataJson;

@Immutable
public enum TestWorldEntrySerializer {
	;

	/*-
	 * We need complex keys for the BiomeDataJson which contains a Map<AreaJson, short[]>
	 */
	private static final Gson GSON = new GsonBuilder().enableComplexMapKeySerialization().create();

	public static WorldMetadataJson readMetaData(InputStream is) {
		return readJson(is, WorldMetadataJson.class);
	}

	public static BiomeDataJson readBiomeData(InputStream is) {
		return readJson(is, BiomeDataJson.class);
	}

	public static EndIslandsJson readEndIslands(InputStream is) {
		return readJson(is, EndIslandsJson.class);
	}

	public static SlimeChunksJson readSlimeChunks(InputStream is) {
		return readJson(is, SlimeChunksJson.class);
	}

	public static CoordinatesCollectionJson readCoordinatesCollection(InputStream is) {
		return readJson(is, CoordinatesCollectionJson.class);
	}

	private static <T> T readJson(InputStream is, Class<T> clazz) {
		try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
			return GSON.fromJson(reader, clazz);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static void writeJson(OutputStream os, Object data) {
		try {
			// do not use the try-with-resource mechanism, because this will
			// also close the output stream
			OutputStreamWriter writer = new OutputStreamWriter(os, StandardCharsets.UTF_8);
			GSON.toJson(data, writer);
			writer.flush();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
