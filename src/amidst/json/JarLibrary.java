package amidst.json;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import amidst.utilties.FileSystemUtils;

public class JarLibrary {
	private String name;
	private List<JarRule> rules = new ArrayList<JarRule>();

	public String getName() {
		return name;
	}

	public List<JarRule> getRules() {
		return rules;
	}

	public File getFile() {
		if (isActive()) {
			File result = FileSystemUtils.getLibraryFile(name);
			if (result != null && result.exists()) {
				return result;
			}
		}
		return null;
	}

	private boolean isActive() {
		if (rules.isEmpty()) {
			return true;
		}

		for (JarRule rule : rules) {
			if (rule.isApplicable() && rule.isAllowed()) {
				return true;
			}
		}

		return false;
	}
}