package amidst.clazz.translator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import amidst.clazz.real.RealClass;
import amidst.clazz.real.RealClassDetector;
import amidst.clazz.symbolic.declaration.SymbolicClassDeclaration;
import amidst.clazz.symbolic.declaration.SymbolicConstructorDeclaration;
import amidst.clazz.symbolic.declaration.SymbolicFieldDeclaration;
import amidst.clazz.symbolic.declaration.SymbolicMethodDeclaration;
import amidst.clazz.symbolic.declaration.SymbolicParameterDeclarationList;
import amidst.clazz.symbolic.declaration.SymbolicParameterDeclarationList.ExecuteOnEnd;
import amidst.clazz.symbolic.declaration.SymbolicParameterDeclarationList.SymbolicParameterDeclarationListBuilder;
import amidst.documentation.NotThreadSafe;

/**
 * While this class is not thread-safe by itself, its product is thread-safe.
 */
@NotThreadSafe
public class CTBuilder {
	@NotThreadSafe
	public class SCDBuilder {
		private String symbolicClassName;
		private boolean isOptional;
		private final List<SymbolicConstructorDeclaration> constructors = new ArrayList<>();
		private final List<SymbolicMethodDeclaration> methods = new ArrayList<>();
		private final List<SymbolicFieldDeclaration> fields = new ArrayList<>();

		private void init(String symbolicClassName, boolean isOptional) {
			this.symbolicClassName = symbolicClassName;
			this.isOptional = isOptional;
		}

		private SymbolicClassDeclaration constructThis() {
			return new SymbolicClassDeclaration(symbolicClassName, isOptional, constructors, methods, fields);
		}

		public CTBuilder next() {
			return new CTBuilder(CTBuilder.this);
		}

		public ClassTranslator construct() {
			return CTBuilder.this.construct();
		}

		public SymbolicParameterDeclarationListBuilder<SCDBuilder> requiredConstructor(final String symbolicName) {
			return constructor(symbolicName, false);
		}

		public SymbolicParameterDeclarationListBuilder<SCDBuilder> optionalConstructor(final String symbolicName) {
			return constructor(symbolicName, true);
		}

		private SymbolicParameterDeclarationListBuilder<SCDBuilder> constructor(
				final String symbolicName,
				final boolean isOptional) {
			return new SymbolicParameterDeclarationListBuilder<>(this, new ExecuteOnEnd() {
				@Override
				public void run(SymbolicParameterDeclarationList parameters) {
					constructors.add(new SymbolicConstructorDeclaration(symbolicName, isOptional, parameters));
				}
			});
		}

		public SymbolicParameterDeclarationListBuilder<SCDBuilder> requiredMethod(
				String symbolicName,
				String realName) {
			return method(symbolicName, realName, false);
		}

		public SymbolicParameterDeclarationListBuilder<SCDBuilder> optionalMethod(
				String symbolicName,
				String realName) {
			return method(symbolicName, realName, true);
		}

		private SymbolicParameterDeclarationListBuilder<SCDBuilder> method(
				final String symbolicName,
				final String realName,
				final boolean isOptional) {
			return new SymbolicParameterDeclarationListBuilder<>(this, new ExecuteOnEnd() {
				@Override
				public void run(SymbolicParameterDeclarationList parameters) {
					methods.add(new SymbolicMethodDeclaration(symbolicName, realName, isOptional, parameters));
				}
			});
		}

		public SCDBuilder requiredField(String symbolicName, String realName) {
			return field(symbolicName, realName, false);
		}

		public SCDBuilder optionalField(String symbolicName, String realName) {
			return field(symbolicName, realName, true);
		}

		private SCDBuilder field(String symbolicName, String realName, boolean isOptional) {
			fields.add(new SymbolicFieldDeclaration(symbolicName, realName, isOptional));
			return this;
		}
	}

	public static CTBuilder newInstance() {
		return new CTBuilder(null);
	}

	private final CTBuilder previous;

	private RealClassDetector detector = null;
	private final SCDBuilder declarationBuilder = new SCDBuilder();

	private CTBuilder(CTBuilder previous) {
		this.previous = previous;
	}

	public CTBuilder ifDetect(Predicate<RealClass> predicate) {
		this.detector = new RealClassDetector(predicate);
		return this;
	}

	public SCDBuilder thenDeclareRequired(String symbolicClassName) {
		return thenDeclare(symbolicClassName, false);
	}

	public SCDBuilder thenDeclareOptional(String symbolicClassName) {
		return thenDeclare(symbolicClassName, true);
	}

	private SCDBuilder thenDeclare(String symbolicClassName, boolean isOptional) {
		if (detector == null) {
			throw new IllegalStateException("can't declare a symbolic class without calling ifDetect before");
		}
		declarationBuilder.init(symbolicClassName, isOptional);
		return declarationBuilder;
	}

	public ClassTranslator construct() {
		return new ClassTranslator(constructResult());
	}

	private Map<RealClassDetector, SymbolicClassDeclaration> constructResult() {
		Map<RealClassDetector, SymbolicClassDeclaration> result = constructPreviousResult();
		result.put(detector, declarationBuilder.constructThis());
		return result;
	}

	private Map<RealClassDetector, SymbolicClassDeclaration> constructPreviousResult() {
		if (previous != null) {
			return previous.constructResult();
		} else {
			return new HashMap<>();
		}
	}
}
