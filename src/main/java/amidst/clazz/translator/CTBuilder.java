package amidst.clazz.translator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import amidst.clazz.real.detector.AllRCD;
import amidst.clazz.real.detector.AnyRCD;
import amidst.clazz.real.detector.FieldFlagsRCD;
import amidst.clazz.real.detector.LongRCD;
import amidst.clazz.real.detector.NumberOfConstructorsRCD;
import amidst.clazz.real.detector.NumberOfFieldsRCD;
import amidst.clazz.real.detector.NumberOfMethodsRCD;
import amidst.clazz.real.detector.RealClassDetector;
import amidst.clazz.real.detector.StringContainingRCD;
import amidst.clazz.real.detector.Utf8EqualToRCD;
import amidst.clazz.real.detector.WildcardByteRCD;
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
	public class RCDBuilder {
		private final List<List<RealClassDetector>> allDetectors = new ArrayList<List<RealClassDetector>>();
		private List<RealClassDetector> detectors = new ArrayList<RealClassDetector>();

		private RealClassDetector constructThis() {
			if (allDetectors.size() == 1) {
				return new AllRCD(allDetectors.get(0));
			} else {
				List<RealClassDetector> result = new ArrayList<RealClassDetector>();
				for (List<RealClassDetector> detectors : allDetectors) {
					result.add(new AllRCD(detectors));
				}
				return new AnyRCD(result);
			}
		}

		public RCDBuilder or() {
			allDetectors.add(detectors);
			detectors = new ArrayList<RealClassDetector>();
			return this;
		}

		public SCDBuilder thenDeclareRequired(String symbolicClassName) {
			return thenDeclare(symbolicClassName, false);
		}

		public SCDBuilder thenDeclareOptional(String symbolicClassName) {
			return thenDeclare(symbolicClassName, true);
		}

		private SCDBuilder thenDeclare(String symbolicClassName, boolean isOptional) {
			allDetectors.add(detectors);
			CTBuilder.this.declarationBuilder.init(symbolicClassName, isOptional);
			return CTBuilder.this.declarationBuilder;
		}

		public RCDBuilder fieldFlags(int flags, int... fieldIndices) {
			detectors.add(new FieldFlagsRCD(flags, fieldIndices));
			return this;
		}

		public RCDBuilder longs(long... longs) {
			detectors.add(new LongRCD(longs));
			return this;
		}

		public RCDBuilder numberOfConstructors(int count) {
			detectors.add(new NumberOfConstructorsRCD(count));
			return this;
		}

		public RCDBuilder numberOfFields(int count) {
			detectors.add(new NumberOfFieldsRCD(count));
			return this;
		}

		public RCDBuilder numberOfMethods(int count) {
			detectors.add(new NumberOfMethodsRCD(count));
			return this;
		}

		public RCDBuilder stringContaining(String string) {
			detectors.add(new StringContainingRCD(string));
			return this;
		}

		public RCDBuilder utf8EqualTo(String utf8) {
			detectors.add(new Utf8EqualToRCD(utf8));
			return this;
		}

		public RCDBuilder wildcardBytes(int[] bytes) {
			detectors.add(new WildcardByteRCD(bytes));
			return this;
		}
	}

	@NotThreadSafe
	public class SCDBuilder {
		private String symbolicClassName;
		private boolean isOptional;
		private final List<SymbolicConstructorDeclaration> constructors = new ArrayList<SymbolicConstructorDeclaration>();
		private final List<SymbolicMethodDeclaration> methods = new ArrayList<SymbolicMethodDeclaration>();
		private final List<SymbolicFieldDeclaration> fields = new ArrayList<SymbolicFieldDeclaration>();

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
			return new SymbolicParameterDeclarationListBuilder<SCDBuilder>(this, new ExecuteOnEnd() {
				@Override
				public void run(SymbolicParameterDeclarationList parameters) {
					constructors.add(new SymbolicConstructorDeclaration(symbolicName, isOptional, parameters));
				}
			});
		}

		public SymbolicParameterDeclarationListBuilder<SCDBuilder> requiredMethod(String symbolicName, String realName) {
			return method(symbolicName, realName, false);
		}

		public SymbolicParameterDeclarationListBuilder<SCDBuilder> optionalMethod(String symbolicName, String realName) {
			return method(symbolicName, realName, true);
		}

		private SymbolicParameterDeclarationListBuilder<SCDBuilder> method(
				final String symbolicName,
				final String realName,
				final boolean isOptional) {
			return new SymbolicParameterDeclarationListBuilder<SCDBuilder>(this, new ExecuteOnEnd() {
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

	private final RCDBuilder detectorBuilder = new RCDBuilder();
	private final SCDBuilder declarationBuilder = new SCDBuilder();

	private CTBuilder(CTBuilder previous) {
		this.previous = previous;
	}

	public RCDBuilder ifDetect() {
		return detectorBuilder;
	}

	public ClassTranslator construct() {
		return new ClassTranslator(constructResult());
	}

	private Map<RealClassDetector, SymbolicClassDeclaration> constructResult() {
		Map<RealClassDetector, SymbolicClassDeclaration> result = constructPreviousResult();
		result.put(detectorBuilder.constructThis(), declarationBuilder.constructThis());
		return result;
	}

	private Map<RealClassDetector, SymbolicClassDeclaration> constructPreviousResult() {
		if (previous != null) {
			return previous.constructResult();
		} else {
			return new HashMap<RealClassDetector, SymbolicClassDeclaration>();
		}
	}
}
