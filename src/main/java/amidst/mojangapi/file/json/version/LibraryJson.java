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

	/**
	 * Note, that multiple rules might be applicable. We take the last
	 * applicable rule. However, this might be wrong so we need to take the most
	 * specific rule? For now this works fine.
	 */
	public boolean isActive(String os) {
		if (rules.isEmpty()) {
			return true;
		}
		boolean result = false;
		for (LibraryRuleJson rule : rules) {
			if (rule.isApplicable(os)) {
				result = rule.isAllowed();
			}
		}
		return result;
	}
}
