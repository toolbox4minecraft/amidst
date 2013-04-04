package amidst.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TagFloat extends Tag<Float> {
	TagFloat(String name, float value) {
		super(Type.TAG_Float, name, value);
	}
	
	TagFloat(String name, DataInputStream dis) throws IOException {
		this(name, dis.readFloat());
	}
	
	void writePayload(DataOutputStream dos) throws IOException {
		dos.writeFloat(value);
	}
}
