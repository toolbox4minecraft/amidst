package amidst.clazz.finder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
import amidst.clazz.symbolic.declaration.ConstructorDeclaration;
import amidst.clazz.symbolic.declaration.ConstructorRCD;
import amidst.clazz.symbolic.declaration.MethodDeclaration;
import amidst.clazz.symbolic.declaration.MethodRCD;
import amidst.clazz.symbolic.declaration.MultiRCD;
import amidst.clazz.symbolic.declaration.ParameterDeclarationList;
import amidst.clazz.symbolic.declaration.PropertyDeclaration;
import amidst.clazz.symbolic.declaration.PropertyRCD;
import amidst.clazz.symbolic.declaration.RealClassPreparer;
import amidst.clazz.symbolic.declaration.ParameterDeclarationList.Builder;
import amidst.clazz.symbolic.declaration.ParameterDeclarationList.ExecuteOnEnd;

public class RCFBuilder {
	public class BCDBuilder {
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

		public BCDBuilder or() {
			allDetectors.add(detectors);
			detectors = new ArrayList<RealClassDetector>();
			return this;
		}

		public BCPBuilder prepare() {
			allDetectors.add(detectors);
			return RCFBuilder.this.preparerBuilder;
		}

		public BCDBuilder fieldFlags(int flags, int... fieldIndices) {
			detectors.add(new FieldFlagsRCD(flags, fieldIndices));
			return this;
		}

		public BCDBuilder longs(long... longs) {
			detectors.add(new LongRCD(longs));
			return this;
		}

		public BCDBuilder numberOfConstructors(int count) {
			detectors.add(new NumberOfConstructorsRCD(count));
			return this;
		}

		public BCDBuilder numberOfFields(int count) {
			detectors.add(new NumberOfFieldsRCD(count));
			return this;
		}

		public BCDBuilder numberOfMethodsAndConstructors(int count) {
			detectors.add(new NumberOfMethodsAndConstructorsRCD(count));
			return this;
		}

		public BCDBuilder strings(String... strings) {
			detectors.add(new StringRCD(strings));
			return this;
		}

		public BCDBuilder utf8s(String... utf8s) {
			detectors.add(new Utf8RCD(utf8s));
			return this;
		}

		public BCDBuilder wildcardBytes(int[] bytes) {
			detectors.add(new WildcardByteRCD(bytes));
			return this;
		}
	}

	public class BCPBuilder {
		private List<RealClassPreparer> preparers = new ArrayList<RealClassPreparer>();

		private RealClassPreparer constructThis() {
			if (preparers.size() == 1) {
				return preparers.get(0);
			} else {
				return new MultiRCD(preparers);
			}
		}

		public RCFBuilder next() {
			return new RCFBuilder(RCFBuilder.this);
		}

		public List<RealClassFinder> construct() {
			return RCFBuilder.this.construct();
		}

		public Builder<BCPBuilder> addConstructor(final String symbolicName) {
			return ParameterDeclarationList.builder(this, new ExecuteOnEnd() {
				@Override
				public void run(ParameterDeclarationList parameters) {
					preparers
							.add(new ConstructorRCD(new ConstructorDeclaration(
									symbolicName, parameters)));
				}
			});
		}

		public Builder<BCPBuilder> addMethod(final String symbolicName,
				final String realName) {
			return ParameterDeclarationList.builder(this, new ExecuteOnEnd() {
				@Override
				public void run(ParameterDeclarationList parameters) {
					preparers.add(new MethodRCD(new MethodDeclaration(
							symbolicName, realName, parameters)));
				}
			});
		}

		public BCPBuilder addProperty(String symbolicName, String realName) {
			preparers.add(new PropertyRCD(new PropertyDeclaration(symbolicName,
					realName)));
			return this;
		}
	}

	public static RCFBuilder builder() {
		return new RCFBuilder(null);
	}

	private RCFBuilder previous;

	private String symbolicClassName;
	private BCDBuilder detectorBuilder = new BCDBuilder();
	private BCPBuilder preparerBuilder = new BCPBuilder();

	private RCFBuilder(RCFBuilder previous) {
		this.previous = previous;
	}

	public RCFBuilder name(String symbolicClassName) {
		this.symbolicClassName = symbolicClassName;
		return this;
	}

	public BCDBuilder detect() {
		return detectorBuilder;
	}

	public List<RealClassFinder> construct() {
		List<RealClassFinder> result;
		if (previous != null) {
			result = previous.construct();
		} else {
			result = new ArrayList<RealClassFinder>();
		}
		result.add(constructThis());
		return result;
	}

	private RealClassFinder constructThis() {
		Objects.requireNonNull(symbolicClassName,
				"a real class finder needs to have a name");
		return new RealClassFinder(symbolicClassName,
				detectorBuilder.constructThis(),
				preparerBuilder.constructThis());
	}
}
