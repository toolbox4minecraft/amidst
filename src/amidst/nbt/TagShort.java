package amidst.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TagShort extends Tag<Short> {
	TagShort(String name, short value) {
		super(Type.TAG_Short, name, value);
	}
	
	TagShort(String name, DataInputStream dis) throws IOException {
		this(name, dis.readShort());
	}
	
	void writePayload(DataOutputStream dos) throws IOException {
		dos.writeShort(value);
	}
}
