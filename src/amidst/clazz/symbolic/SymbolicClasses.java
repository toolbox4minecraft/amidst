package amidst.clazz.symbolic;

import java.util.Map;

import amidst.clazz.real.RealClass;

public class SymbolicClasses {
	public static Map<String, SymbolicClass> createClasses(
			ClassLoader classLoader,
			Map<String, RealClass> realClassesBySymbolicClassName) {
		return new SymbolicClassGraphBuilder(classLoader,
				realClassesBySymbolicClassName).create();
	}

	private SymbolicClasses() {
	}
}
