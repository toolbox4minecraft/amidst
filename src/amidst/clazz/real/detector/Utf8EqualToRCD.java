package amidst.clazz.real.detector;

import amidst.clazz.real.RealClass;
import amidst.documentation.Immutable;

@Immutable
public class Utf8EqualToRCD extends RealClassDetector {
	private final String utf8;

	public Utf8EqualToRCD(String utf8) {
		this.utf8 = utf8;
	}

	@Override
	public boolean detect(RealClass realClass) {
		return realClass.searchForUtf8EqualTo(utf8);
	}
}
