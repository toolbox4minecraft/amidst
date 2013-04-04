package amidst.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class TagByteArray extends Tag<byte[]> {
	TagByteArray(String name, byte[] value) {
		super(Type.TAG_Byte_Array, name, value);
	}
	
	TagByteArray(String name, DataInputStream dis) throws IOException {
		this(name, readPayload(dis));
	}
	
	private static byte[] readPayload(DataInputStream dis) throws IOException {
		int length = dis.readInt();
		byte[] ba = new byte[length];
		dis.readFully(ba);
		return ba;
	}
	
	void writePayload(DataOutputStream dos) throws IOException {
		dos.writeInt(value.length);
		dos.write(value);
	}
	
	@Override
	void serialize(PrintStream ps, int indent) {
		serializePrefix(ps, indent, ": [" + value.length + " bytes]");
	}
}
