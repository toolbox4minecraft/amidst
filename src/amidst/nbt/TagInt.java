package amidst.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TagInt extends Tag<Integer> {
	TagInt(String name, int value) {
		super(Type.TAG_Int, name, value);
	}
	
	TagInt(String name, DataInputStream dis) throws IOException {
		this(name, dis.readInt());
	}
	
	void writePayload(DataOutputStream dos) throws IOException {
		dos.writeInt(value);
	}
}
