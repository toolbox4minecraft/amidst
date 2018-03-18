package amidst.gui.main;

import java.io.IOException;

import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.parsing.FormatException;
import amidst.parsing.json.JsonReader;

@Immutable
public enum UpdateInformationRetriever {
	;

	private static final String UPDATE_INFORMATION_JSON_URL = "https://treer.github.io/amidstest/api/update-information.json";

	@NotNull
	public static UpdateInformationJson retrieve() throws FormatException, IOException {
		return JsonReader.readLocation(UPDATE_INFORMATION_JSON_URL, UpdateInformationJson.class);
	}
}
