package amidst.clazz.real.detector;

import amidst.clazz.real.RealClass;
import amidst.documentation.Immutable;

@Immutable
public class StringRCD extends RealClassDetector {
	private final String[] strings;

	public StringRCD(String... strings) {
		this.strings = strings;
	}

	@Override
	public boolean detect(RealClass realClass) {
		for (String element : strings) {
			if (!realClass.searchForStringContaining(element)) {
				return false;
			}
		}
		return true;
	}
}
