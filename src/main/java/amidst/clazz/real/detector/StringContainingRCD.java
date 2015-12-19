package amidst.clazz.real.detector;

import amidst.clazz.real.RealClass;
import amidst.documentation.Immutable;

@Immutable
public class StringContainingRCD extends RealClassDetector {
	private final String string;

	public StringContainingRCD(String string) {
		this.string = string;
	}

	@Override
	public boolean detect(RealClass realClass) {
		return realClass.searchForStringContaining(string);
	}
}
