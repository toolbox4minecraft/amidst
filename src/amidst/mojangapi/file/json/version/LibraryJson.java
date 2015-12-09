package amidst.mojangapi.file.json.version;

import java.util.Collections;
import java.util.List;

import amidst.documentation.GsonConstructor;

public class LibraryJson {
	private String name;
	private List<LibraryRuleJson> rules = Collections.emptyList();

	@GsonConstructor
	public LibraryJson() {
	}

	public String getName() {
		return name;
	}

	public boolean isActive() {
		if (rules.isEmpty()) {
			return true;
		}
		for (LibraryRuleJson rule : rules) {
			if (rule.isApplicable() && rule.isAllowed()) {
				return true;
			}
		}
		return false;
	}
}