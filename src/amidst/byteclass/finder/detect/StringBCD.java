package amidst.byteclass.finder.detect;

import amidst.byteclass.ByteClass;

public class StringBCD extends ByteClassDetector {
	private String[] strings;

	public StringBCD(String... strings) {
		this.strings = strings;
	}

	@Override
	public boolean detect(ByteClass byteClass) {
		for (String element : strings) {
			if (!byteClass.searchForString(element)) {
				return false;
			}
		}
		return true;
	}
}
