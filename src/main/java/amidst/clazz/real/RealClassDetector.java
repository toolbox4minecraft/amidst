package amidst.clazz.real;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import amidst.documentation.Immutable;

@Immutable
public class RealClassDetector {
	private final BiPredicate<RealClass, Map<String, String>> predicate;

	public RealClassDetector(BiPredicate<RealClass, Map<String, String>> predicate) {
		this.predicate = predicate;
	}

	public Optional<RealClass> firstMatching(List<RealClass> realClasses, Map<String, String> mappedNames) {
		return realClasses.stream().filter(c -> predicate.test(c, mappedNames)).findFirst();
	}

	public List<RealClass> allMatching(List<RealClass> realClasses, Map<String, String> mappedNames) {
		return realClasses.stream().filter(c -> predicate.test(c, mappedNames)).collect(Collectors.toList());
	}
}
