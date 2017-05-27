package amidst.mojangapi.file;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.mojangapi.file.json.version.LibraryJson;
import amidst.mojangapi.file.json.version.LibraryRuleJson;
import amidst.mojangapi.file.json.version.LibraryRuleOsJson;
import amidst.mojangapi.file.json.version.VersionJson;

@Immutable
public class LibraryService {
	private static final String ACTION_ALLOW = "allow";

	/**
	 * Note, that multiple rules might be applicable. We take the last
	 * applicable rule. However, this might be wrong so we need to take the most
	 * specific rule? For now this works fine.
	 */
	public boolean isLibraryActive(LibraryJson libraryJson, String os, String version) {
		List<LibraryRuleJson> rules = libraryJson.getRules();
		if (rules.isEmpty()) {
			return true;
		}
		boolean result = false;
		for (LibraryRuleJson rule : rules) {
			if (isApplicable(os, version, rule)) {
				result = isAllowed(rule);
			}
		}
		return result;
	}

	private boolean isApplicable(String os, String version, LibraryRuleJson rule) {
		LibraryRuleOsJson osRule = rule.getOs();
		return osRule == null || isApplicable(os, version, osRule);
	}

	private boolean isApplicable(String os, String version, LibraryRuleOsJson osRule) {
		String nameInJson = osRule.getName();
		String versionInJson = osRule.getVersion();
		return nameInJson.equals(os) && (versionInJson == null || Pattern.matches(versionInJson, version));
	}

	public boolean isAllowed(LibraryRuleJson rule) {
		return rule.getAction().equals(ACTION_ALLOW);
	}

	@NotNull
	public List<URL> getLibraryUrls(File librariesDirectory, VersionJson versionJson) {
		return LibraryFinder.getLibraryUrls(librariesDirectory, versionJson.getLibraries());
	}
}
