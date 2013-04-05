package amidst.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class TagList<T> extends SequenceTagBase<T> {
	Type listType;
	
	/** Create a new TAG_List NBT tag.
	 *
	 * @param name name for the new tag or null to create an unnamed tag.
	 * @param value list of tags to add to the new tag.
	 */
	public TagList(String name, Tag<T>[] value) {
		super(Type.TAG_List, name, value);
		this.listType = value[0].type;
	}
	
	/** Create a new TAG_List with an empty list. Use {@link TagList#addTag(Tag)} to add tags later.
	 *
	 * @param name name for this tag or null to create an unnamed tag.
	 * @param listType type of the elements in this empty list.
	 */
	public TagList(String name, Type listType) {
		super(Type.TAG_List, name, new Tag[0]);
		this.listType = listType;
	}
	
	TagList(String name, DataInputStream dis) throws IOException {
		super(Type.TAG_List, name, null);
		listType = Type.fromByte(dis.readByte());
		value = new Tag[dis.readInt()];
		for (int i = 0; i < value.length; i++)
			value[i] = listType.readFrom(null, dis);
	}
	
	void writePayload(DataOutputStream dos) throws IOException {
		dos.writeByte(this.listType.ordinal());
		dos.writeInt(value.length);
		for (Tag tag : value)
			tag.writePayload(dos);
	}
	
	/** Add a tag to a TAG_List
	 */
	public void addTag(Tag tag) {
		insertTag(tag, value.length);
	}

	/** Additional setValue method for empty lists
	 * @param listType
	 */
	public void setValue(Type listType) {
		value = new Tag[0];
		this.listType = listType;
	}
	
	@Override
	void serialize(PrintStream ps, int indent) {
		serializePrefix(ps, indent, ": ", value.length, " entries of type ", listType);
		serializeEntries(ps, indent);
	}
}
