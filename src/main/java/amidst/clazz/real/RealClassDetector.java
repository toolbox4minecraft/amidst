package amidst.clazz.real;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import amidst.documentation.Immutable;

@Immutable
public class RealClassDetector {
	private final Predicate<RealClass> predicate;

	public RealClassDetector(Predicate<RealClass> predicate) {
		this.predicate = predicate;
	}

	public Optional<RealClass> firstMatching(List<RealClass> realClasses) {
		return realClasses.stream().filter(predicate).findFirst();
	}

	public List<RealClass> allMatching(List<RealClass> realClasses) {
		return realClasses.stream().filter(predicate).collect(Collectors.toList());
	}
}
