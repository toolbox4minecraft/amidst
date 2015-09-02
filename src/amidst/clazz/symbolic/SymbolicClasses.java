package amidst.clazz.symbolic;

import java.util.Map;

import amidst.clazz.symbolic.declaration.SymbolicClassDeclaration;

public class SymbolicClasses {
	private SymbolicClasses() {
	}

	public static Map<String, SymbolicClass> from(
			Map<SymbolicClassDeclaration, String> realClassNamesBySymbolicClassDeclaration,
			ClassLoader classLoader) {
		return new SymbolicClassGraphBuilder(classLoader,
				realClassNamesBySymbolicClassDeclaration).create();
	}
}
