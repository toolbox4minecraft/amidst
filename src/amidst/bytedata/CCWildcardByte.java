package amidst.bytedata;

import amidst.minecraft.Minecraft;

public class CCWildcardByte extends ClassChecker {
	private int[] checkData;

	public CCWildcardByte(String name, int[] checkData) {
		super(name);
		this.checkData = checkData;
	}

	@Override
	public boolean isMatching(ByteClass byteClass) {
		return isMatching(byteClass.getData());
	}

	private boolean isMatching(byte[] data) {
		int loopLimit = data.length + 1 - checkData.length;
		for (int startIndex = 0; startIndex < loopLimit; startIndex++) {
			if (isMatchingAt(data, startIndex)) {
				return true;
			}
		}
		return false;
	}

	private boolean isMatchingAt(byte[] data, int startIndex) {
		for (int offset = 0; offset < checkData.length; offset++) {
			if (checkData[offset] != -1
					&& data[startIndex + offset] != (byte) checkData[offset]) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void execute(Minecraft mc, ByteClass byteClass) {
		mc.registerClass(getName(), byteClass);
	}
}
