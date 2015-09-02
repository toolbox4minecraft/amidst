package amidst.clazz.real.finder.detect;

import amidst.clazz.real.RealClass;

public class WildcardByteRCD extends RealClassDetector {
	private int[] bytes;

	public WildcardByteRCD(int[] bytes) {
		this.bytes = bytes;
	}

	@Override
	public boolean detect(RealClass realClass) {
		return isMatching(realClass.getClassData());
	}

	private boolean isMatching(byte[] data) {
		int loopLimit = data.length + 1 - bytes.length;
		for (int startIndex = 0; startIndex < loopLimit; startIndex++) {
			if (isMatchingAt(data, startIndex)) {
				return true;
			}
		}
		return false;
	}

	private boolean isMatchingAt(byte[] data, int startIndex) {
		for (int offset = 0; offset < bytes.length; offset++) {
			if (bytes[offset] != -1
					&& data[startIndex + offset] != (byte) bytes[offset]) {
				return false;
			}
		}
		return true;
	}
}
