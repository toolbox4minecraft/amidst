package amidst.clazz.real.finder.detect;

import amidst.clazz.real.RealClass;

public class Utf8RCD extends RealClassDetector {
	private String[] utf8s;

	public Utf8RCD(String... utf8s) {
		this.utf8s = utf8s;
	}

	@Override
	public boolean detect(RealClass byteClass) {
		for (String element : utf8s) {
			if (!byteClass.searchForUtf(element)) {
				return false;
			}
		}
		return true;
	}
}
