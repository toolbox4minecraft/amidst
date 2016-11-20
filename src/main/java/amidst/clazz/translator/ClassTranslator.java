package amidst.clazz.translator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import amidst.clazz.real.RealClass;
import amidst.clazz.real.RealClassDetector;
import amidst.clazz.symbolic.declaration.SymbolicClassDeclaration;
import amidst.documentation.Immutable;

@Immutable
public class ClassTranslator {
	public static CTBuilder builder() {
		return CTBuilder.newInstance();
	}

	private final Map<RealClassDetector, SymbolicClassDeclaration> translations;

	public ClassTranslator(Map<RealClassDetector, SymbolicClassDeclaration> translations) {
		this.translations = translations;
	}

	public Map<SymbolicClassDeclaration, List<RealClass>> translateToAllMatching(List<RealClass> realClasses) {
		Map<SymbolicClassDeclaration, List<RealClass>> result = new HashMap<>();
		for (Entry<RealClassDetector, SymbolicClassDeclaration> entry : translations.entrySet()) {
			SymbolicClassDeclaration declaration = entry.getValue();
			List<RealClass> allMatching = entry.getKey().allMatching(realClasses);
			if (result.containsKey(declaration)) {
				result.get(declaration).addAll(allMatching);
			} else {
				result.put(declaration, allMatching);
			}
		}
		return result;
	}

	public Map<SymbolicClassDeclaration, String> translate(List<RealClass> realClasses) throws ClassNotFoundException {
		Map<SymbolicClassDeclaration, String> result = new HashMap<>();
		for (Entry<RealClassDetector, SymbolicClassDeclaration> entry : translations.entrySet()) {
			addResult(
					result,
					entry.getValue(),
					entry.getKey().firstMatching(realClasses).map(RealClass::getRealClassName));
		}
		return result;
	}

	private void addResult(
			Map<SymbolicClassDeclaration, String> result,
			SymbolicClassDeclaration declaration,
			Optional<String> realClassName) throws ClassNotFoundException {
		if (!realClassName.isPresent()) {
			declaration.handleNoMatch();
		} else if (result.containsKey(declaration)) {
			declaration.handleMultipleMatches(result.get(declaration), realClassName.get());
		} else {
			declaration.handleMatch(realClassName.get());
			result.put(declaration, realClassName.get());
		}
	}
}
