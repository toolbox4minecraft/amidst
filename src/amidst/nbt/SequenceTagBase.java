package amidst.nbt;

import java.io.PrintStream;

public abstract class SequenceTagBase<T> extends Tag<Tag<T>[]> {
	SequenceTagBase(Type type, String name, Tag<T>[] value) {
		super(type, name, value);
	}
	
	public abstract void addTag(Tag<T> tag);
	
	/** Add a tag to a TAG_List or a TAG_Compound at the specified index.
	 */
	public void insertTag(Tag<T> tag, int index) {
		if (value.length > 0 && (this instanceof TagList && tag.type != ((TagList) this).listType))
			throw new IllegalArgumentException();
		if (index > value.length)
			throw new IndexOutOfBoundsException();
		
		Tag<T>[] newValue = new Tag[value.length + 1];
		System.arraycopy(value, 0, newValue, 0, index);
		newValue[index] = tag;
		System.arraycopy(value, index, newValue, index + 1, value.length - index);

		value = newValue;
	}

	/** Remove a tag from a TAG_List or a TAG_Compound at the specified index.
	 *
	 * @return the removed tag
	 */
	public Tag<T> removeTag(int index) {
		if (type != Type.TAG_List && type != Type.TAG_Compound)
			throw new RuntimeException();
		
		Tag<T> victim = value[index];
		Tag<T>[] newValue = new Tag[value.length - 1];
		System.arraycopy(value, 0, newValue, 0, index);
		index++;
		System.arraycopy(value, index, newValue, index - 1, value.length - index);
		
		value = newValue;
		return victim;
	}
	
	/** Remove a tag from a TAG_List or a TAG_Compound. If the tag is not a child of this tag then nested tags are searched.
	 *
	 * @param tag tag to look for
	 */
	public void removeSubTag(Tag<T> tag) {
		if (tag == null) return;
		
		for (int i = 0; i < value.length; i++) {
			if (value[i] == tag) {
				removeTag(i);
				return;
			} else {
				if (value[i] instanceof SequenceTagBase) {
					((SequenceTagBase<T>) value[i]).removeSubTag(tag);
				}
			}
		}
	}
	
	/**
	 * Find the first nested tag with specified name in a TAG_List or TAG_Compound after a tag with the same name.
	 *
	 * @param name the name to look for. May be null to look for unnamed tags.
	 * @param found the previously found tag with the same name.
	 * @return the first nested tag that has the specified name after the previously found tag.
	 */
	public Tag<T> findNextTagByName(String name, Tag<T> found) {
		for (Tag<T> subTag : value) {
			if ((subTag.name == null && name == null) //End tag
					|| (subTag.name != null && subTag.name.equals(name))) {
				return subTag;
			} else if (subTag instanceof TagCompound) {
				Tag<T> newFound = ((TagCompound) subTag).findTagByName(name);
				if (newFound != null && newFound != found)
					return newFound;
			}
		}
		return null;
	}
	
	void serializeEntries(PrintStream ps, int indent) {
		serializeIndented(ps, indent, "{");
		for (Tag st : value)
			st.serialize(ps, indent + 1);
		serializeIndented(ps, indent, "}");
	}
}
