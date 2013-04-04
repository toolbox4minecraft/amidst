package amidst.nbt;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * NBT IO class
 *
 * @see <a href="http://www.minecraft.net/docs/NBT.txt">Online NBT specification</a>
 */
public abstract class Tag<T> {
	public final Type type;
	public final String name;
	T value;
	
	/**
	 * Enum for the tag types.
	 */
	public enum Type {
		TAG_End(TagEnd.class),
		TAG_Byte(TagByte.class),
		TAG_Short(TagShort.class),
		TAG_Int(TagInt.class),
		TAG_Long(TagLong.class),
		TAG_Float(TagFloat.class),
		TAG_Double(TagDouble.class),
		TAG_Byte_Array(TagByteArray.class),
		TAG_String(TagString.class),
		TAG_List(TagList.class),
		TAG_Compound(TagCompound.class),
		TAG_Int_Array(TagIntArray.class);
		
		final Class<? extends Tag> clazz;
		
		private Type(Class<? extends Tag> clazz) {
			 this.clazz = clazz;
		}
		
		public Tag readFrom(String name, DataInputStream dis) {
			try {
				Constructor<? extends Tag> c = clazz.getDeclaredConstructor(String.class, DataInputStream.class);
				return c.newInstance(name, dis);
			} catch (NoSuchMethodException e) { //TODO: maybe java 7 for multicatch?
				throw new RuntimeException(name, e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(name, e);
			} catch (InstantiationException e) {
				throw new RuntimeException(name, e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(name, e);
			}
		}
		
		public static Type fromByte(Byte type) {
			return Type.values()[type];
		}
	}
	
	Tag(Type type, String name, T value) {
		this.type = type;
		this.name = name;
		this.value = value;
	}
	
	public T getValue() {
		return value;
	}
	
	public void setValue(T newValue) {
		value = newValue;
	}
	
	/**
	 * Read a tag and its nested tags from an InputStream.
	 *
	 * @param is stream to read from, like a FileInputStream
	 * @return NBT tag or structure read from the InputStream
	 * @throws java.io.IOException if there was no valid NBT structure in the InputStream or if another IOException occurred.
	 */
	public static TagCompound readFrom(InputStream is) throws IOException {
		DataInputStream dis = new DataInputStream(new GZIPInputStream(is));
		Type type = Type.fromByte(dis.readByte());
		if (type != Type.TAG_Compound)
			throw new IOException("Root tags have to be Compound tags");
		
		TagCompound root = (TagCompound) Type.TAG_Compound.readFrom(dis.readUTF(), dis);
		is.close();
		return root;
	}
	
	/**
	 * Read a tag and its nested tags from an InputStream.
	 *
	 * @param os stream to write to, like a FileOutputStream
	 * @throws IOException if this is not a valid NBT structure or if any IOException occurred.
	 */
	public void writeTo(OutputStream os) throws IOException {
		GZIPOutputStream gzos;
		DataOutputStream dos = new DataOutputStream(gzos = new GZIPOutputStream(os));
		dos.writeByte(type.ordinal());
		if (type != Type.TAG_End) {
			dos.writeUTF(name);
			writePayload(dos);
		}
		gzos.flush();
		gzos.close();
	}
	
	abstract void writePayload(DataOutputStream dos) throws IOException;
	
	/** Print the NBT structure to System.out
	 */
	public void serialize() {
		serialize(System.out, 0);
	}

	/** Prints an indented sequence of objects to a PrintStream, terminated by a newline
	 * @param ps      PrintStream to serialize to
	 * @param indent  Indentation level (2 spaces per level are printed)
	 * @param line    Things to serialize after the indentation
	 */
	static void serializeIndented(PrintStream ps, int indent, Object... line) {
		for (int i = 0; i < indent; i++)
			ps.print("  ");
		for (Object thing : line)
			if (thing != null)
				if (thing instanceof Object[])
					for (Object underThing : (Object[]) thing)
						ps.print(underThing);
				else
					ps.print(thing);
		ps.println();
	}
	
	/** Prints this Tag’s information and the sequence of Objects to a Prinstream, terminated by a newline
	 * @param ps     PrintStream to serialize to
	 * @param indent Indentation level (2 spaces per level are printed)
	 * @param line   Things to serialize after the indentation and prefix
	 */
	void serializePrefix(PrintStream ps, int indent, Object... line) {
		if (type == Type.TAG_End) return;
		String suffix = (name == null) ? null : "(\"" + name + "\")";
		serializeIndented(ps, indent, type, suffix, line);
	}
	
	/** Serializes this Tag to a PrintStream
	 * Default implementation for value-like Tags. Overridden in sequence Tags
	 * @param ps     PrintStream to serialize to
	 * @param indent Indentation level (2 spaces per level are printed)
	 */
	void serialize(PrintStream ps, int indent) {
		serializePrefix(ps, indent, ": ", value);
	}
}