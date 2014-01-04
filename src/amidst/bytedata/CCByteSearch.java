package amidst.bytedata;

import amidst.minecraft.Minecraft;

public class CCByteSearch extends ClassChecker {
	private byte[] checkData;
	public CCByteSearch(String publicName, byte[] data) {
		super(publicName);
		checkData = data;
	}
	@Override
	public void check(Minecraft m, ByteClass bClass) {
		byte[] data = bClass.getData();
		
		for (int i = 0; i < data.length + 1 - checkData.length; i++) {
			boolean searching = true;
			int sIndex = 0;
			while (searching) {
				if (data[i + sIndex] != checkData[sIndex])
					searching = false;
				sIndex++;
				if (searching && (sIndex == checkData.length)) {
					isComplete = true;
					m.registerClass(publicName, bClass);
					return;
				}
			}
		}
	}
}
