package amidst.bytedata;

import amidst.minecraft.Minecraft;

public class CCFloatMatch extends ClassChecker {
	private float[] checkData;
	public CCFloatMatch(String name, float... data) {
		super(name);
		checkData = data;
	}
	@Override
	public void check(Minecraft m, ByteClass bClass) {
		boolean isMatch = true;
		for (int i = 0; i < checkData.length; i++) {
			isMatch &= bClass.searchForFloat(checkData[i]);
		}
		if (isMatch) {
			m.registerClass(publicName, bClass);
			isComplete = true;
		}
	}
}
