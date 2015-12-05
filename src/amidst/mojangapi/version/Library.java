package amidst.mojangapi.version;

import java.util.List;

public class Library {
	private String name;
	private List<LibraryRule> rules;

	public Library() {
		// no-argument constructor for gson
	}

	public String getName() {
		return name;
	}

	public boolean isActive() {
		if (rules.isEmpty()) {
			return true;
		}
		for (LibraryRule rule : rules) {
			if (rule.isApplicable() && rule.isAllowed()) {
				return true;
			}
		}
		return false;
	}
}