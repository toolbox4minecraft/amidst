package amidst.clazz.real.finder.detect;

import amidst.clazz.real.RealClass;

public class StringRCD extends RealClassDetector {
	private String[] strings;

	public StringRCD(String... strings) {
		this.strings = strings;
	}

	@Override
	public boolean detect(RealClass byteClass) {
		for (String element : strings) {
			if (!byteClass.searchForString(element)) {
				return false;
			}
		}
		return true;
	}
}
