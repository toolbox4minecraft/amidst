package amidst.clazz.real.detector;

import amidst.clazz.real.RealClass;

public class StringRCD extends RealClassDetector {
	private String[] strings;

	public StringRCD(String... strings) {
		this.strings = strings;
	}

	@Override
	public boolean detect(RealClass realClass) {
		for (String element : strings) {
			if (!realClass.searchForString(element)) {
				return false;
			}
		}
		return true;
	}
}
