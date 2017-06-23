package amidst.mojangapi.file.json.version;

import java.util.Collections;
import java.util.List;

import amidst.documentation.GsonObject;
import amidst.documentation.Immutable;

@Immutable
@GsonObject(ignoreUnknown=true)
public class LibraryJson {
	private volatile String name;
	private volatile List<LibraryRuleJson> rules = Collections.emptyList();

	public LibraryJson() {
	}

	public String getName() {
		return name;
	}

	public List<LibraryRuleJson> getRules() {
		return rules;
	}
}
