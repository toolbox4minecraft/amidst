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
import amidst.clazz.real.detector.StringRCD;
import amidst.clazz.real.detector.Utf8RCD;
import amidst.clazz.real.detector.WildcardByteRCD;
import amidst.clazz.symbolic.declaration.SymbolicClassDeclaration;
import amidst.clazz.symbolic.declaration.SymbolicConstructorDeclaration;
import amidst.clazz.symbolic.declaration.SymbolicFieldDeclaration;
import amidst.clazz.symbolic.declaration.SymbolicMethodDeclaration;
import amidst.clazz.symbolic.declaration.SymbolicParameterDeclarationList;
import amidst.clazz.symbolic.declaration.SymbolicParameterDeclarationList.Builder;
import amidst.clazz.symbolic.declaration.SymbolicParameterDeclarationList.ExecuteOnEnd;

public class CTBuilder {
	public class RCDBuilder {
		private List<List<RealClassDetector>> allDetectors = new ArrayList<List<RealClassDetector>>();
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

		public SCDBuilder thenDeclare(String symbolicClassName) {
			allDetectors.add(detectors);
			CTBuilder.this.declarationBuilder
					.setSymbolicClassName(symbolicClassName);
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

		public RCDBuilder strings(String... strings) {
			detectors.add(new StringRCD(strings));
			return this;
		}

		public RCDBuilder utf8s(String... utf8s) {
			detectors.add(new Utf8RCD(utf8s));
			return this;
		}

		public RCDBuilder wildcardBytes(int[] bytes) {
			detectors.add(new WildcardByteRCD(bytes));
			return this;
		}
	}

	public class SCDBuilder {
		private String symbolicClassName;
		private List<SymbolicConstructorDeclaration> constructors = new ArrayList<SymbolicConstructorDeclaration>();
		private List<SymbolicMethodDeclaration> methods = new ArrayList<SymbolicMethodDeclaration>();
		private List<SymbolicFieldDeclaration> fields = new ArrayList<SymbolicFieldDeclaration>();

		private void setSymbolicClassName(String symbolicClassName) {
			this.symbolicClassName = symbolicClassName;
		}

		private SymbolicClassDeclaration constructThis() {
			return new SymbolicClassDeclaration(symbolicClassName,
					constructors, methods, fields);
		}

		public CTBuilder next() {
			return new CTBuilder(CTBuilder.this);
		}

		public ClassTranslator construct() {
			return CTBuilder.this.construct();
		}

		public Builder<SCDBuilder> constructor(final String symbolicName) {
			return SymbolicParameterDeclarationList.builder(this,
					new ExecuteOnEnd() {
						@Override
						public void run(
								SymbolicParameterDeclarationList parameters) {
							constructors
									.add(new SymbolicConstructorDeclaration(
											symbolicName, parameters));
						}
					});
		}

		public Builder<SCDBuilder> method(final String symbolicName,
				final String realName) {
			return SymbolicParameterDeclarationList.builder(this,
					new ExecuteOnEnd() {
						@Override
						public void run(
								SymbolicParameterDeclarationList parameters) {
							methods.add(new SymbolicMethodDeclaration(
									symbolicName, realName, parameters));
						}
					});
		}

		public SCDBuilder field(String symbolicName, String realName) {
			fields.add(new SymbolicFieldDeclaration(symbolicName, realName));
			return this;
		}
	}

	public static CTBuilder newInstance() {
		return new CTBuilder(null);
	}

	private CTBuilder previous;

	private RCDBuilder detectorBuilder = new RCDBuilder();
	private SCDBuilder declarationBuilder = new SCDBuilder();

	private CTBuilder(CTBuilder previous) {
		this.previous = previous;
	}

	public CTBuilder name() {
		return this;
	}

	public RCDBuilder ifDetect() {
		return detectorBuilder;
	}

	public ClassTranslator construct() {
		return new ClassTranslator(constructResult());
	}

	private Map<RealClassDetector, SymbolicClassDeclaration> constructResult() {
		Map<RealClassDetector, SymbolicClassDeclaration> result = constructPreviousResult();
		result.put(detectorBuilder.constructThis(),
				declarationBuilder.constructThis());
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
