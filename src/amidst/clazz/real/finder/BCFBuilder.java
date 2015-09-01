package amidst.clazz.real.finder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import amidst.clazz.real.ParameterDeclarationList;
import amidst.clazz.real.ParameterDeclarationList.Builder;
import amidst.clazz.real.ParameterDeclarationList.ExecuteOnEnd;
import amidst.clazz.real.finder.detect.AllBCD;
import amidst.clazz.real.finder.detect.AnyBCD;
import amidst.clazz.real.finder.detect.ByteClassDetector;
import amidst.clazz.real.finder.detect.FieldFlagsBCD;
import amidst.clazz.real.finder.detect.LongBCD;
import amidst.clazz.real.finder.detect.NumberOfConstructorsBCD;
import amidst.clazz.real.finder.detect.NumberOfFieldsBCD;
import amidst.clazz.real.finder.detect.NumberOfMethodsAndConstructorsBCD;
import amidst.clazz.real.finder.detect.StringBCD;
import amidst.clazz.real.finder.detect.Utf8BCD;
import amidst.clazz.real.finder.detect.WildcardByteBCD;
import amidst.clazz.real.finder.prepare.ByteClassPreparer;
import amidst.clazz.real.finder.prepare.ConstructorBCP;
import amidst.clazz.real.finder.prepare.MethodBCP;
import amidst.clazz.real.finder.prepare.MultiBCP;
import amidst.clazz.real.finder.prepare.PropertyBCP;

public class BCFBuilder {
	public class BCDBuilder {
		private List<List<ByteClassDetector>> allDetectors = new ArrayList<List<ByteClassDetector>>();
		private List<ByteClassDetector> detectors = new ArrayList<ByteClassDetector>();

		private ByteClassDetector constructThis() {
			if (allDetectors.size() == 1) {
				return new AllBCD(allDetectors.get(0));
			} else {
				List<ByteClassDetector> result = new ArrayList<ByteClassDetector>();
				for (List<ByteClassDetector> detectors : allDetectors) {
					result.add(new AllBCD(detectors));
				}
				return new AnyBCD(result);
			}
		}

		public BCDBuilder or() {
			allDetectors.add(detectors);
			detectors = new ArrayList<ByteClassDetector>();
			return this;
		}

		public BCPBuilder prepare() {
			allDetectors.add(detectors);
			return BCFBuilder.this.preparerBuilder;
		}

		public BCDBuilder fieldFlags(int flags, int... fieldIndices) {
			detectors.add(new FieldFlagsBCD(flags, fieldIndices));
			return this;
		}

		public BCDBuilder longs(long... longs) {
			detectors.add(new LongBCD(longs));
			return this;
		}

		public BCDBuilder numberOfConstructors(int count) {
			detectors.add(new NumberOfConstructorsBCD(count));
			return this;
		}

		public BCDBuilder numberOfFields(int count) {
			detectors.add(new NumberOfFieldsBCD(count));
			return this;
		}

		public BCDBuilder numberOfMethodsAndConstructors(int count) {
			detectors.add(new NumberOfMethodsAndConstructorsBCD(count));
			return this;
		}

		public BCDBuilder strings(String... strings) {
			detectors.add(new StringBCD(strings));
			return this;
		}

		public BCDBuilder utf8s(String... utf8s) {
			detectors.add(new Utf8BCD(utf8s));
			return this;
		}

		public BCDBuilder wildcardBytes(int[] bytes) {
			detectors.add(new WildcardByteBCD(bytes));
			return this;
		}
	}

	public class BCPBuilder {
		private List<ByteClassPreparer> preparers = new ArrayList<ByteClassPreparer>();

		private ByteClassPreparer constructThis() {
			if (preparers.size() == 1) {
				return preparers.get(0);
			} else {
				return new MultiBCP(preparers);
			}
		}

		public BCFBuilder next() {
			return new BCFBuilder(BCFBuilder.this);
		}

		public List<ByteClassFinder> construct() {
			return BCFBuilder.this.construct();
		}

		public Builder<BCPBuilder> addConstructor(final String symbolicName) {
			return ParameterDeclarationList.builder(this, new ExecuteOnEnd() {
				@Override
				public void run(ParameterDeclarationList parameters) {
					preparers.add(new ConstructorBCP(symbolicName, parameters));
				}
			});
		}

		public Builder<BCPBuilder> addMethod(final String symbolicName,
				final String realName) {
			return ParameterDeclarationList.builder(this, new ExecuteOnEnd() {
				@Override
				public void run(ParameterDeclarationList parameters) {
					preparers.add(new MethodBCP(symbolicName, realName,
							parameters));
				}
			});
		}

		public BCPBuilder addProperty(String symbolicName, String realName) {
			preparers.add(new PropertyBCP(symbolicName, realName));
			return this;
		}
	}

	public static BCFBuilder builder() {
		return new BCFBuilder(null);
	}

	private BCFBuilder previous;

	private String symbolicClassName;
	private BCDBuilder detectorBuilder = new BCDBuilder();
	private BCPBuilder preparerBuilder = new BCPBuilder();

	private BCFBuilder(BCFBuilder previous) {
		this.previous = previous;
	}

	public BCFBuilder name(String symbolicClassName) {
		this.symbolicClassName = symbolicClassName;
		return this;
	}

	public BCDBuilder detect() {
		return detectorBuilder;
	}

	public List<ByteClassFinder> construct() {
		List<ByteClassFinder> result;
		if (previous != null) {
			result = previous.construct();
		} else {
			result = new ArrayList<ByteClassFinder>();
		}
		result.add(constructThis());
		return result;
	}

	private ByteClassFinder constructThis() {
		Objects.requireNonNull(symbolicClassName,
				"a byte class finder needs to have a name");
		return new ByteClassFinder(symbolicClassName,
				detectorBuilder.constructThis(),
				preparerBuilder.constructThis());
	}
}
