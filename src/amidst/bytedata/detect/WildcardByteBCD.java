package amidst.bytedata.detect;

import amidst.bytedata.ByteClass;

public class WildcardByteBCD extends ByteClassDetector {
	private int[] bytes;

	public WildcardByteBCD(int[] bytes) {
		this.bytes = bytes;
	}

	@Override
	public boolean detect(ByteClass byteClass) {
		return isMatching(byteClass.getData());
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
