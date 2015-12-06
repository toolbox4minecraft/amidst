package amidst.mojangapi.version;

import java.util.Collections;
import java.util.List;

public class LibraryJson {
	private String name;
	private List<LibraryRuleJson> rules = Collections.emptyList();

	public LibraryJson() {
		// no-argument constructor for gson
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