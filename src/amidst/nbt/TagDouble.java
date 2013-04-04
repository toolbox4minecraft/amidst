package amidst.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TagDouble extends Tag<Double> {
	TagDouble(String name, double value) {
		super(Type.TAG_Double, name, value);
	}
	
	TagDouble(String name, DataInputStream dis) throws IOException {
		this(name, dis.readDouble());
	}
	
	void writePayload(DataOutputStream dos) throws IOException {
		dos.writeDouble(value);
	}
}
