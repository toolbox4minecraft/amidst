package amidst.bytedata;

import amidst.minecraft.Minecraft;

public class CCByteSearch extends ClassChecker {
	private byte[] checkData;

	public CCByteSearch(String name, byte[] checkData) {
		super(name);
		this.checkData = checkData;
	}

	@Override
	public void check(Minecraft m, ByteClass bClass) {
		byte[] data = bClass.getData();
		int loopLimit = data.length - (checkData.length - 1);
		for (int startIndex = 0; startIndex < loopLimit; startIndex++) {
			if (isMatchingAt(data, startIndex)) {
				complete();
				m.registerClass(getName(), bClass);
				return;
			}
		}
	}

	private boolean isMatchingAt(byte[] data, int startIndex) {
		for (int offset = 0; offset < checkData.length; offset++) {
			if (data[startIndex + offset] != checkData[offset]) {
				return false;
			}
		}
		return true;
	}
}
