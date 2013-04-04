package amidst.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class TagCompound extends SequenceTagBase<Object> {
	/**
	 * Create a new TAG_Compound NBT tag.
	 *
	 * @param name name for the new tag or null to create an unnamed tag.
	 * @param value list of tags to add to the new tag.
	 */
	public TagCompound(String name, Tag[] value) {
		super(Type.TAG_Compound, name, value);
	}
	
	TagCompound(String name, DataInputStream dis) throws IOException {
		this(name, readPayload(dis));
	}
	
	private static Tag[] readPayload(DataInputStream dis) throws IOException {
		Type stt;
		List<Tag> tags = new ArrayList<Tag>();
		do {
			stt = Type.fromByte(dis.readByte());
			String name = null;
			if (stt != Type.TAG_End) {
				name = dis.readUTF();
			}
			tags.add(stt.readFrom(name, dis));
		} while (stt != Type.TAG_End);
		
		return tags.toArray(new Tag[tags.size()]);
	}
	
	void writePayload(DataOutputStream dos) throws IOException {
		for (Tag subTag : value) {
			dos.writeByte(type.ordinal());
			if (subTag.type != Type.TAG_End) {
				dos.writeUTF(subTag.name);
				subTag.writePayload(dos);
			}
		}
	}
	
	/** Add a tag to a TAG_Compound.
	 * 
	 * We need to add the tag BEFORE the end, or the new tag gets placed after the TAG_End, messing up the data.
	 * TAG_End MUST be kept at the very end of the TAG_Compound.
	 */
	public void addTag(Tag tag) {
		insertTag(tag, value.length - 1);
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
	
	@Override
	void serialize(PrintStream ps, int indent) {
		serializePrefix(ps, indent, ": ", value.length - 1, " entries");
		serializeEntries(ps, indent);
	}
}
