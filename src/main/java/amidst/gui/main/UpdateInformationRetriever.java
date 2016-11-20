package amidst.gui.main;

import java.io.IOException;
import java.io.Reader;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.mojangapi.file.URIUtils;

@Immutable
public enum UpdateInformationRetriever {
	;

	private static final String UPDATE_INFORMATION_JSON_URL = "https://toolbox4minecraft.github.io/amidst/api/update-information.json";
	private static final Gson GSON = new Gson();

	@NotNull
	public static UpdateInformationJson retrieve() throws IOException {
		try (Reader theReader = URIUtils.newReader(UPDATE_INFORMATION_JSON_URL)) {
			UpdateInformationJson result = GSON.fromJson(theReader, UpdateInformationJson.class);
			if (result != null) {
				return result;
			} else {
				throw new IOException("result was null");
			}
		} catch (JsonSyntaxException | JsonIOException e) {
			throw new IOException(e);
		}
	}
}
