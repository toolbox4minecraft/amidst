package amidst.bytedata;

import amidst.minecraft.Minecraft;


public class CCByteMatch extends ClassChecker {
	private byte[] checkData;
	private int dataOffset;
	public CCByteMatch(String name, byte[] data, int offset) { // FIXME : Dead class
		super(name);
		checkData = data;
		dataOffset = offset;
	}
	public boolean check(ByteClass bClass) {
		byte[] data = bClass.getData();
		
		if (data.length < dataOffset + checkData.length)
			return false;
		
		for (int i = 0; i < checkData.length; i++) {
			if (checkData[i] != data[i + dataOffset])
				return false;
		}
		return true;
	}
	@Override
	public void check(Minecraft m, ByteClass bClass) {
		// TODO Auto-generated method stub
		
	}
}
