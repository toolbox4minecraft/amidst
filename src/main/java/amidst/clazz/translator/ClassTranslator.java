package amidst.clazz.translator;

import java.util.Collections;
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

	private final List<Map.Entry<RealClassDetector, SymbolicClassDeclaration>> translations;

	public ClassTranslator(List<Map.Entry<RealClassDetector, SymbolicClassDeclaration>> translations) {
		this.translations = translations;
	}

	public Map<SymbolicClassDeclaration, List<RealClass>> translateToAllMatching(List<RealClass> realClasses) {
		Map<SymbolicClassDeclaration, List<RealClass>> result = new HashMap<>();
		Map<String, String> foundNames = new HashMap<>();
		Map<String, String> foundNamesView = Collections.unmodifiableMap(foundNames);
		for (Entry<RealClassDetector, SymbolicClassDeclaration> entry : translations) {
			SymbolicClassDeclaration declaration = entry.getValue();
			List<RealClass> allMatching = entry.getKey().allMatching(realClasses, foundNamesView);
			if (result.containsKey(declaration)) {
				result.get(declaration).addAll(allMatching);
			} else {
				result.put(declaration, allMatching);
				allMatching.stream()
					.findFirst()
					.ifPresent(c -> foundNames.put(declaration.getSymbolicClassName(), c.getRealClassName()));
			}
		}
		return result;
	}

	public Map<SymbolicClassDeclaration, String> translate(List<RealClass> realClasses) throws ClassNotFoundException {
		Map<SymbolicClassDeclaration, String> result = new HashMap<>();
		Map<String, String> foundNames = new HashMap<>();
		Map<String, String> foundNamesView = Collections.unmodifiableMap(foundNames);
		for (Entry<RealClassDetector, SymbolicClassDeclaration> entry : translations) {
			Optional<String> realClassName = entry.getKey().firstMatching(realClasses, foundNamesView).map(RealClass::getRealClassName);
			SymbolicClassDeclaration declaration = entry.getValue();
			realClassName.ifPresent(name -> foundNames.put(declaration.getSymbolicClassName(), name));
			addResult(
					result,
					declaration,
					realClassName);
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
