package amidst.bytedata;

import amidst.minecraft.Minecraft;

public class CCWildcardByteSearch extends ClassChecker {
	private int[] checkData;

	public CCWildcardByteSearch(String name, int[] checkData) {
		super(name);
		this.checkData = checkData;
	}

	@Override
	public void check(Minecraft mc, ByteClass bClass) {
		byte[] data = bClass.getData();
		int loopLimit = data.length + 1 - checkData.length;
		for (int startIndex = 0; startIndex < loopLimit; startIndex++) {
			if (isMatchingAt(data, startIndex)) {
				complete();
				mc.registerClass(getName(), bClass);
				return;
			}
		}
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
}
