package amidst.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TagLong extends Tag<Long> {
	TagLong(String name, long value) {
		super(Type.TAG_Long, name, value);
	}
	
	TagLong(String name, DataInputStream dis) throws IOException {
		this(name, dis.readLong());
	}
	
	void writePayload(DataOutputStream dos) throws IOException {
		dos.writeLong(value);
	}
}
