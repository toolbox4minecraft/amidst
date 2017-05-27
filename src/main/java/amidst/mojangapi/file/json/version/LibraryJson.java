package amidst.mojangapi.file.json.version;

import java.util.Collections;
import java.util.List;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;

@Immutable
public class LibraryJson {
	private volatile String name;
	private volatile List<LibraryRuleJson> rules = Collections.emptyList();

	@GsonConstructor
	public LibraryJson() {
	}

	public String getName() {
		return name;
	}

	public List<LibraryRuleJson> getRules() {
		return rules;
	}
}
