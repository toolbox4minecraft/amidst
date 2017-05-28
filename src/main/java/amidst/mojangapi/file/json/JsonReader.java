package amidst.mojangapi.file.json;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.mojangapi.file.FormatException;
import amidst.mojangapi.file.URIUtils;

/**
 * This is a utility class used to read JSON data. Please use this class only to
 * read JSON data provided by Mojang, because it throws a
 * MojangApiParsingException when an error occurs.
 */
@Immutable
public enum JsonReader {
	;

	private static final Gson GSON = new Gson();

	@NotNull
	public static <T> T readLocation(File location, Class<T> clazz) throws FormatException, IOException {
		return readReader(URIUtils.newReader(location), clazz);
	}

	@NotNull
	public static <T> T readLocation(URL location, Class<T> clazz) throws FormatException, IOException {
		return readReader(URIUtils.newReader(location), clazz);
	}

	@NotNull
	public static <T> T readLocation(String location, Class<T> clazz) throws FormatException, IOException {
		return readReader(URIUtils.newReader(location), clazz);
	}

	@NotNull
	private static <T> T readReader(Reader reader, Class<T> clazz) throws FormatException, IOException {
		try (Reader theReader = reader) {
			T result = GSON.fromJson(theReader, clazz);
			if (result != null) {
				return result;
			} else {
				throw new FormatException("result was null");
			}
		} catch (JsonSyntaxException e) {
			throw new FormatException(e);
		} catch (JsonIOException e) {
			throw new IOException(e);
		}
	}

	@NotNull
	public static <T> T readString(String string, Class<T> clazz) throws FormatException {
		try {
			T result = GSON.fromJson(string, clazz);
			if (result != null) {
				return result;
			} else {
				throw new FormatException("result was null");
			}
		} catch (JsonSyntaxException e) {
			throw new FormatException(e);
		}
	}
}
