package amidst.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class TagIntArray extends Tag<int[]> {
	TagIntArray(String name, int[] value) {
		super(Type.TAG_Int_Array, name, value);
	}
	
	TagIntArray(String name, DataInputStream dis) throws IOException {
		this(name, readPayload(dis));
	}
	
	private static int[] readPayload(DataInputStream dis) throws IOException {
		int length = dis.readInt();
		int[] ia = new int[length];
		for (int i=0; i<length; i++)
			ia[i] = dis.readInt();
		return ia;
	}
	
	void writePayload(DataOutputStream dos) throws IOException {
		dos.writeInt(value.length);
		for (int v : value)
			dos.writeInt(v);
	}
	
	@Override
	void serialize(PrintStream ps, int indent) {
		serializePrefix(ps, indent, ": [", value.length, " integers]");
	}
}
