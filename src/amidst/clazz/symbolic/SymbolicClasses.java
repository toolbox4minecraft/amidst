package amidst.clazz.symbolic;

import java.util.Map;

import amidst.clazz.real.RealClass;
import amidst.clazz.symbolic.declaration.SymbolicClassDeclaration;

public class SymbolicClasses {
	private SymbolicClasses() {
	}

	public static Map<String, SymbolicClass> fromRealClasses(
			Map<SymbolicClassDeclaration, RealClass> realClassesBySymbolicClassDeclaration,
			ClassLoader classLoader) {
		return new SymbolicClassGraphBuilder(classLoader,
				realClassesBySymbolicClassDeclaration).create();
	}
}
