package amidst.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TagString extends Tag<String> {
	TagString(String name, String value) {
		super(Type.TAG_String, name, value);
	}
	
	TagString(String name, DataInputStream dis) throws IOException {
		this(name, dis.readUTF());
	}
	
	void writePayload(DataOutputStream dos) throws IOException {
		dos.writeUTF(value);
	}
}
