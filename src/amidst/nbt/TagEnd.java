package amidst.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TagEnd extends Tag<Object> {
	public TagEnd(String name) {
		super(Type.TAG_End, name, null);
	}
	
	TagEnd(String name, DataInputStream dis) throws IOException {
		this(name);
	}
	
	void writePayload(DataOutputStream dos) throws IOException {}
	
	@Override
	public void setValue(Object newValue) {
		if (value != null)
			throw new IllegalArgumentException();
		value = newValue;
	}
}
