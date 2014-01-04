package amidst.bytedata;

import amidst.minecraft.Minecraft;


public class CCLongMatch extends ClassChecker {
	private long[] checkData;
	public CCLongMatch(String name, long... data) {
		super(name);
		checkData = data;
	}
	@Override
	public void check(Minecraft m, ByteClass bClass) {
		boolean isMatch = true;
		for (int i = 0; i < checkData.length; i++) {
			isMatch &= bClass.searchForLong(checkData[i]);
		}
		if (isMatch) {
			m.registerClass(publicName, bClass);
			isComplete = true;
		}
	}
}
