package amidst.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TagByte extends Tag<Byte> {
	TagByte(String name, byte value) {
		super(Type.TAG_Byte, name, value);
	}
	
	TagByte(String name, DataInputStream dis) throws IOException {
		this(name, dis.readByte());
	}
	
	void writePayload(DataOutputStream dos) throws IOException {
		dos.writeByte(value);
	}
}
