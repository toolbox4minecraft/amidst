package amidst.bytedata;

import java.io.UnsupportedEncodingException;

import amidst.minecraft.Minecraft;


public class CCStringMatch extends ClassChecker {
	private String checkData;
	public CCStringMatch(String name, String data) {
		super(name);
		checkData = data;
	}
	public void check(Minecraft m, ByteClass bClass) {
		if (bClass.searchForString(checkData)) {
			m.registerClass(publicName, bClass);
			isComplete = true;
		}
	}
	
}
