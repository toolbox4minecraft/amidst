package amidst.clazz.finder;

import java.util.ArrayList;
import java.util.List;

import amidst.clazz.real.detector.AllRCD;
import amidst.clazz.real.detector.AnyRCD;
import amidst.clazz.real.detector.FieldFlagsRCD;
import amidst.clazz.real.detector.LongRCD;
import amidst.clazz.real.detector.NumberOfConstructorsRCD;
import amidst.clazz.real.detector.NumberOfFieldsRCD;
import amidst.clazz.real.detector.NumberOfMethodsAndConstructorsRCD;
import amidst.clazz.real.detector.RealClassDetector;
import amidst.clazz.real.detector.StringRCD;
import amidst.clazz.real.detector.Utf8RCD;
import amidst.clazz.real.detector.WildcardByteRCD;
import amidst.clazz.symbolic.declaration.SymbolicConstructorDeclaration;
import amidst.clazz.symbolic.declaration.SymbolicMethodDeclaration;
import amidst.clazz.symbolic.declaration.SymbolicParameterDeclarationList;
import amidst.clazz.symbolic.declaration.SymbolicParameterDeclarationList.Builder;
import amidst.clazz.symbolic.declaration.SymbolicParameterDeclarationList.ExecuteOnEnd;
import amidst.clazz.symbolic.declaration.SymbolicPropertyDeclaration;
import amidst.clazz.symbolic.declaration.SymbolicClassDeclaration;

public class CFBuilder {
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

		public SCDBuilder declare(String symbolicClassName) {
			allDetectors.add(detectors);
			CFBuilder.this.declarationBuilder
					.setSymbolicClassName(symbolicClassName);
			return CFBuilder.this.declarationBuilder;
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

		public RCDBuilder numberOfMethodsAndConstructors(int count) {
			detectors.add(new NumberOfMethodsAndConstructorsRCD(count));
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
		private List<SymbolicPropertyDeclaration> properties = new ArrayList<SymbolicPropertyDeclaration>();

		private void setSymbolicClassName(String symbolicClassName) {
			this.symbolicClassName = symbolicClassName;
		}

		private SymbolicClassDeclaration constructThis() {
			return new SymbolicClassDeclaration(symbolicClassName,
					constructors, methods, properties);
		}

		public CFBuilder next() {
			return new CFBuilder(CFBuilder.this);
		}

		public List<ClassFinder> construct() {
			return CFBuilder.this.construct();
		}

		public Builder<SCDBuilder> addConstructor(final String symbolicName) {
			return SymbolicParameterDeclarationList.builder(this, new ExecuteOnEnd() {
				@Override
				public void run(SymbolicParameterDeclarationList parameters) {
					constructors.add(new SymbolicConstructorDeclaration(symbolicName,
							parameters));
				}
			});
		}

		public Builder<SCDBuilder> method(final String symbolicName,
				final String realName) {
			return SymbolicParameterDeclarationList.builder(this, new ExecuteOnEnd() {
				@Override
				public void run(SymbolicParameterDeclarationList parameters) {
					methods.add(new SymbolicMethodDeclaration(symbolicName, realName,
							parameters));
				}
			});
		}

		public SCDBuilder property(String symbolicName, String realName) {
			properties.add(new SymbolicPropertyDeclaration(symbolicName, realName));
			return this;
		}
	}

	public static CFBuilder builder() {
		return new CFBuilder(null);
	}

	private CFBuilder previous;

	private RCDBuilder detectorBuilder = new RCDBuilder();
	private SCDBuilder declarationBuilder = new SCDBuilder();

	private CFBuilder(CFBuilder previous) {
		this.previous = previous;
	}

	public CFBuilder name() {
		return this;
	}

	public RCDBuilder detect() {
		return detectorBuilder;
	}

	public List<ClassFinder> construct() {
		List<ClassFinder> result;
		if (previous != null) {
			result = previous.construct();
		} else {
			result = new ArrayList<ClassFinder>();
		}
		result.add(constructThis());
		return result;
	}

	private ClassFinder constructThis() {
		return new ClassFinder(detectorBuilder.constructThis(),
				declarationBuilder.constructThis());
	}
}
