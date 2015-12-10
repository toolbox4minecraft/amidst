package amidst.clazz.symbolic;

import java.util.Map;

import amidst.clazz.symbolic.declaration.SymbolicClassDeclaration;
import amidst.documentation.Immutable;

@Immutable
public enum SymbolicClasses {
	;

	public static Map<String, SymbolicClass> from(
			Map<SymbolicClassDeclaration, String> realClassNamesBySymbolicClassDeclaration,
			ClassLoader classLoader) {
		return new SymbolicClassGraphBuilder(classLoader,
				realClassNamesBySymbolicClassDeclaration).create();
	}
}
