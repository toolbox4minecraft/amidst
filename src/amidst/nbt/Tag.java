package amidst.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * NBT IO class
 *
 * @see <a href="http://www.minecraft.net/docs/NBT.txt">Online NBT specification</a>
 */
public class Tag {
	private final Type type;
	private Type listType = null;
	private final String name;
	private Object value;
	
	/**
	 * Enum for the tag types.
	 */
	public enum Type {
		TAG_End,
		TAG_Byte,
		TAG_Short,
		TAG_Int,
		TAG_Long,
		TAG_Float,
		TAG_Double,
		TAG_Byte_Array,
		TAG_String,
		TAG_List,
		TAG_Compound,
		TAG_Int_Array
	}
	
	/**
	 * Create a new TAG_List or TAG_Compound NBT tag.
	 *
	 * @param type either TAG_List or TAG_Compound
	 * @param name name for the new tag or null to create an unnamed tag.
	 * @param value list of tags to add to the new tag.
	 */
	public Tag(Type type, String name, Tag[] value) {
		this(type, name, (Object) value);
	}
	
	/**
	 * Create a new TAG_List with an empty list. Use {@link Tag#addTag(Tag)} to add tags later.
	 *
	 * @param name name for this tag or null to create an unnamed tag.
	 * @param listType type of the elements in this empty list.
	 */
	public Tag(String name, Type listType) {
		this(Type.TAG_List, name, listType);
	}
	
	/**
	 * Create a new NBT tag.
	 *
	 * @param type any value from the {@link Type} enum.
	 * @param name name for the new tag or null to create an unnamed tag.
	 * @param value an object that fits the tag type or a {@link Type} to create an empty TAG_List with this list type.
	 */
	public Tag(Type type, String name, Object value) {
		switch (type) {
			case TAG_End:
				if (value != null)
					throw new IllegalArgumentException();
				break;
			case TAG_Byte:
				if (!(value instanceof Byte))
					throw new IllegalArgumentException();
				break;
			case TAG_Short:
				if (!(value instanceof Short))
					throw new IllegalArgumentException();
				break;
			case TAG_Int:
				if (!(value instanceof Integer))
					throw new IllegalArgumentException();
				break;
			case TAG_Long:
				if (!(value instanceof Long))
					throw new IllegalArgumentException();
				break;
			case TAG_Float:
				if (!(value instanceof Float))
					throw new IllegalArgumentException();
				break;
			case TAG_Double:
				if (!(value instanceof Double))
					throw new IllegalArgumentException();
				break;
			case TAG_Byte_Array:
				if (!(value instanceof byte[]))
					throw new IllegalArgumentException();
				break;
			case TAG_String:
				if (!(value instanceof String))
					throw new IllegalArgumentException();
				break;
			case TAG_List:
				if (value instanceof Type) {
					this.listType = (Type) value;
					value = new Tag[0];
				} else {
					if (!(value instanceof Tag[]))
						throw new IllegalArgumentException();
					this.listType = (((Tag[]) value)[0]).getType();
				}
				break;
			case TAG_Compound:
				if (!(value instanceof Tag[]))
					throw new IllegalArgumentException();
				break;
			default:
				throw new IllegalArgumentException();
			//TODO: Int_Array
		}
		this.type = type;
		this.name = name;
		this.value = value;
	}
	
	public Type getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}
	
	public Object getValue() {
		return value;
	}
	
	public void setValue(Object newValue) {
		switch (type) {
			case TAG_End:
				if (value != null)
					throw new IllegalArgumentException();
				break;
			case TAG_Byte:
				if (!(value instanceof Byte))
					throw new IllegalArgumentException();
				break;
			case TAG_Short:
				if (!(value instanceof Short))
					throw new IllegalArgumentException();
				break;
			case TAG_Int:
				if (!(value instanceof Integer))
					throw new IllegalArgumentException();
				break;
			case TAG_Long:
				if (!(value instanceof Long))
					throw new IllegalArgumentException();
				break;
			case TAG_Float:
				if (!(value instanceof Float))
					throw new IllegalArgumentException();
				break;
			case TAG_Double:
				if (!(value instanceof Double))
					throw new IllegalArgumentException();
				break;
			case TAG_Byte_Array:
				if (!(value instanceof byte[]))
					throw new IllegalArgumentException();
				break;
			case TAG_String:
				if (!(value instanceof String))
					throw new IllegalArgumentException();
				break;
			case TAG_List:
				if (value instanceof Type) {
					this.listType = (Type) value;
					value = new Tag[0];
				} else {
					if (!(value instanceof Tag[]))
						throw new IllegalArgumentException();
					this.listType = (((Tag[]) value)[0]).getType();
				}
				break;
			case TAG_Compound:
				if (!(value instanceof Tag[]))
					throw new IllegalArgumentException();
				break;
			default:
				throw new IllegalArgumentException();
			//TODO: Int_Array
		}
		
		value = newValue;
	}
	
	public Type getListType() {
		return listType;
	}
	
	/**
	 * Add a tag to a TAG_List or a TAG_Compound.
	 */
	public void addTag(Tag tag) {
		if (type != Type.TAG_List && type != Type.TAG_Compound)
			throw new RuntimeException();
		Tag[] subtags = (Tag[]) value;
		
		int index = subtags.length;
		
		//For TAG_Compund entries, we need to add the tag BEFORE the end,
		//or the new tag gets placed after the TAG_End, messing up the data.
		//TAG_End MUST be kept at the very end of the TAG_Compound.
		if(type == Type.TAG_Compound) index--;
		insertTag(tag, index);
	}
	
	/**
	 * Add a tag to a TAG_List or a TAG_Compound at the specified index.
	 */
	public void insertTag(Tag tag, int index) {
		if (type != Type.TAG_List && type != Type.TAG_Compound)
			throw new RuntimeException();
		Tag[] subtags = (Tag[]) value;
		if (subtags.length > 0)
			if (type == Type.TAG_List && tag.getType() != getListType())
				throw new IllegalArgumentException();
		if (index > subtags.length)
			throw new IndexOutOfBoundsException();
		Tag[] newValue = new Tag[subtags.length + 1];
		System.arraycopy(subtags, 0, newValue, 0, index);
		newValue[index] = tag;
		System.arraycopy(subtags, index, newValue, index + 1, subtags.length - index);
		value = newValue;
	}
	
	/**
	 * Remove a tag from a TAG_List or a TAG_Compound at the specified index.
	 *
	 * @return the removed tag
	 */
	public Tag removeTag(int index) {
		if (type != Type.TAG_List && type != Type.TAG_Compound)
			throw new RuntimeException();
		Tag[] subtags = (Tag[]) value;
		Tag victim = subtags[index];
		Tag[] newValue = new Tag[subtags.length - 1];
		System.arraycopy(subtags, 0, newValue, 0, index);
		index++;
		System.arraycopy(subtags, index, newValue, index - 1, subtags.length - index);
		value = newValue;
		return victim;
	}
	
	/**
	 * Remove a tag from a TAG_List or a TAG_Compound. If the tag is not a child of this tag then nested tags are searched.
	 *
	 * @param tag tag to look for
	 */
	public void removeSubTag(Tag tag) {
		if (type != Type.TAG_List && type != Type.TAG_Compound)
			throw new RuntimeException();
		if (tag == null)
			return;
		Tag[] subtags = (Tag[]) value;
		for (int i = 0; i < subtags.length; i++) {
			if (subtags[i] == tag) {
				removeTag(i);
				return;
			} else {
				if (subtags[i].type == Type.TAG_List || subtags[i].type == Type.TAG_Compound) {
					subtags[i].removeSubTag(tag);
				}
			}
		}
	}
	
	/**
	 * Find the first nested tag with specified name in a TAG_Compound.
	 *
	 * @param name the name to look for. May be null to look for unnamed tags.
	 * @return the first nested tag that has the specified name.
	 */
	public Tag findTagByName(String name) {
		return findNextTagByName(name, null);
	}
	
	/**
	 * Find the first nested tag with specified name in a TAG_List or TAG_Compound after a tag with the same name.
	 *
	 * @param name the name to look for. May be null to look for unnamed tags.
	 * @param found the previously found tag with the same name.
	 * @return the first nested tag that has the specified name after the previously found tag.
	 */
	public Tag findNextTagByName(String name, Tag found) {
		if (type != Type.TAG_List && type != Type.TAG_Compound)
			return null;
		Tag[] subtags = (Tag[]) value;
		for (Tag subtag : subtags) {
			if ((subtag.name == null && name == null) || (subtag.name != null && subtag.name.equals(name))) {
				return subtag;
			} else {
				Tag newFound = subtag.findTagByName(name);
				if (newFound != null)
					if (newFound == found)
						continue;
					else
						return newFound;
			}
		}
		return null;
	}
	
	/**
	 * Read a tag and its nested tags from an InputStream.
	 *
	 * @param is stream to read from, like a FileInputStream
	 * @return NBT tag or structure read from the InputStream
	 * @throws java.io.IOException if there was no valid NBT structure in the InputStream or if another IOException occurred.
	 */
	public static Tag readFrom(InputStream is) throws IOException {
		DataInputStream dis = new DataInputStream(new GZIPInputStream(is));
		byte type = dis.readByte();
		Tag tag;
		
		if (type == 0) {
			tag = new Tag(Type.TAG_End, null, null);
		} else {
			tag = new Tag(Type.values()[type], dis.readUTF(), readPayload(dis, type));
		}
		
		dis.close();
		
		return tag;
	}
	
	private static Object readPayload(DataInputStream dis, byte type) throws IOException {
		switch (Type.values()[type]) {
			case TAG_End:
				return null;
			case TAG_Byte:
				return dis.readByte();
			case TAG_Short:
				return dis.readShort();
			case TAG_Int:
				return dis.readInt();
			case TAG_Long:
				return dis.readLong();
			case TAG_Float:
				return dis.readFloat();
			case TAG_Double:
				return dis.readDouble();
			case TAG_Byte_Array:
				int length = dis.readInt();
				byte[] ba = new byte[length];
				dis.readFully(ba);
				return ba;
			case TAG_String:
				return dis.readUTF();
			case TAG_List:
				byte lt = dis.readByte();
				int ll = dis.readInt();
				Tag[] lo = new Tag[ll];
				for (int i = 0; i < ll; i++) {
					lo[i] = new Tag(Type.values()[lt], null, readPayload(dis, lt));
				}
				if (lo.length == 0)
					return Type.values()[lt];
				else
					return lo;
			case TAG_Compound:
				byte stt;
				Tag[] tags = new Tag[0];
				do {
					stt = dis.readByte();
					String name = null;
					if (stt != 0) {
						name = dis.readUTF();
					}
					Tag[] newTags = new Tag[tags.length + 1];
					System.arraycopy(tags, 0, newTags, 0, tags.length);
					newTags[tags.length] = new Tag(Type.values()[stt], name, readPayload(dis, stt));
					tags = newTags;
				} while (stt != 0);
				return tags;
			//TODO: Int_Array
		}
		return null;
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

	private void writePayload(DataOutputStream dos) throws IOException {
		switch (type) {
			case TAG_End:
				break;
			case TAG_Byte:
				dos.writeByte((Byte) value);
				break;
			case TAG_Short:
				dos.writeShort((Short) value);
				break;
			case TAG_Int:
				dos.writeInt((Integer) value);
				break;
			case TAG_Long:
				dos.writeLong((Long) value);
				break;
			case TAG_Float:
				dos.writeFloat((Float) value);
				break;
			case TAG_Double:
				dos.writeDouble((Double) value);
				break;
			case TAG_Byte_Array:
				byte[] ba = (byte[]) value;
				dos.writeInt(ba.length);
				dos.write(ba);
				break;
			case TAG_String:
				dos.writeUTF((String) value);
				break;
			case TAG_List:
				Tag[] list = (Tag[]) value;
				dos.writeByte(getListType().ordinal());
				dos.writeInt(list.length);
				for (Tag tt : list) {
					tt.writePayload(dos);
				}
				break;
			case TAG_Compound:
				Tag[] subtags = (Tag[]) value;
				for (Tag st : subtags) {
					Tag subtag = st;
					Type type = subtag.getType();
					dos.writeByte(type.ordinal());
					if (type != Type.TAG_End) {
						dos.writeUTF(subtag.getName());
						subtag.writePayload(dos);
					}
				}
				break;
			//TODO: Int_Array
		}
	}
	
	/**
	 * Print the NBT structure to System.out
	 */
	public void print() {
		print(this, 0);
	}
	
	private void indent(int indent) {
		for (int i = 0; i < indent; i++) {
			System.out.print("   ");
		}
	}
	
	private void print(Tag t, int indent) {
		Type type = t.getType();
		if (type == Type.TAG_End)
			return;
		String name = t.getName();
		indent(indent);
		System.out.print(t.getType());
		if (name != null)
			System.out.print("(\"" + t.getName() + "\")");
		if (type == Type.TAG_Byte_Array) {
			byte[] b = (byte[]) t.getValue();
			System.out.println(": [" + b.length + " bytes]");
		} else if (type == Type.TAG_List) {
			Tag[] subtags = (Tag[]) t.getValue();
			System.out.println(": " + subtags.length + " entries of type " + t.getListType());
			for (Tag st : subtags) {
				print(st, indent + 1);
			}
			indent(indent);
			System.out.println("}");
		} else if (type == Type.TAG_Compound) {
			Tag[] subtags = (Tag[]) t.getValue();
			System.out.println(": " + (subtags.length - 1) + " entries");
			indent(indent);
			System.out.println("{");
			for (Tag st : subtags) {
				print(st, indent + 1);
			}
			indent(indent);
			System.out.println("}");
		} else {
			System.out.println(": " + t.getValue());
		}
	}
}