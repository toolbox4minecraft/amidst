package amidst.clazz.real.finder.detect;

import amidst.clazz.real.ByteClass;

public class Utf8BCD extends ByteClassDetector {
	private String[] utf8s;

	public Utf8BCD(String... utf8s) {
		this.utf8s = utf8s;
	}

	@Override
	public boolean detect(ByteClass byteClass) {
		for (String element : utf8s) {
			if (!byteClass.searchForUtf(element)) {
				return false;
			}
		}
		return true;
	}
}
