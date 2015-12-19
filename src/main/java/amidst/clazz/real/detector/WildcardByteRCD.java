package amidst.clazz.real.detector;

import amidst.clazz.real.RealClass;
import amidst.documentation.Immutable;

@Immutable
public class WildcardByteRCD extends RealClassDetector {
	private final int[] bytes;

	public WildcardByteRCD(int[] bytes) {
		this.bytes = bytes;
	}

	@Override
	public boolean detect(RealClass realClass) {
		return realClass.isClassDataWildcardMatching(bytes);
	}
}
