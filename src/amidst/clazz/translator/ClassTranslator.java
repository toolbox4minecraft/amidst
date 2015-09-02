package amidst.clazz.translator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import amidst.clazz.real.RealClass;
import amidst.clazz.real.detector.RealClassDetector;
import amidst.clazz.symbolic.declaration.SymbolicClassDeclaration;
import amidst.logging.Log;

public class ClassTranslator {
	public static CTBuilder builder() {
		return CTBuilder.newInstance();
	}

	private Map<RealClassDetector, SymbolicClassDeclaration> translations;

	public ClassTranslator(
			Map<RealClassDetector, SymbolicClassDeclaration> translations) {
		this.translations = translations;
	}

	public Map<SymbolicClassDeclaration, RealClass> translate(
			List<RealClass> realClasses) {
		Map<SymbolicClassDeclaration, RealClass> result = new HashMap<SymbolicClassDeclaration, RealClass>();
		for (Entry<RealClassDetector, SymbolicClassDeclaration> entry : translations
				.entrySet()) {
			addResult(result, entry.getValue(),
					entry.getKey().firstMatching(realClasses));
		}
		return result;
	}

	private void addResult(Map<SymbolicClassDeclaration, RealClass> result,
			SymbolicClassDeclaration declaration, RealClass realClass) {
		if (realClass != null) {
			if (!result.containsKey(declaration)) {
				result.put(declaration, realClass);
			}
			Log.debug("Found: " + realClass.getRealClassName() + " as "
					+ declaration.getSymbolicClassName());
		} else {
			Log.debug("Missing: " + declaration.getSymbolicClassName());
		}
	}
}
